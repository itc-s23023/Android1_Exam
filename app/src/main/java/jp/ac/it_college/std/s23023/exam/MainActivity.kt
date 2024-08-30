package jp.ac.it_college.std.s23023.exam

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.ac.it_college.std.s23023.exam.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val DEBUG_TAG = "Exam"
        private const val WEATHER_INFO_URL =
            "https://api.openweathermap.org/data/2.5/forecast?lang=ja"
        private const val APP_ID = BuildConfig.apiKey
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            lvCityList.apply {
                adapter = CityListAdapter(City.list) {
                    val urlString = "$WEATHER_INFO_URL&q=${it.q}&appid=$APP_ID"
                    receiveWeatherInfo(urlString)
                }
                val manager = LinearLayoutManager(this@MainActivity)
                layoutManager = manager
                addItemDecoration(
                    DividerItemDecoration(this@MainActivity, manager.orientation)
                )
            }
        }
    }

    @UiThread
    private fun receiveWeatherInfo(urlString: String) {
        val backgroundReceiver = WeatherInfoBackgroundReceiver(urlString)
        val executeService = Executors.newSingleThreadExecutor()
        val future = executeService.submit(backgroundReceiver)
        val result = future.get()
        showWeatherInfo(result)
    }

    @UiThread
    private fun showWeatherInfo(result: String) {
        val root = JSONObject(result)
        val cityName = root.getJSONObject("city").getString("name")
        val list = root.getJSONArray("list")

        // 明日の日付を計算
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDate = dateFormat.format(calendar.time)

        // 明日の日付に一致する天気予報を探す
        val forecastsForTomorrow = mutableListOf<JSONObject>()
        for (i in 0 until list.length()) {
            val forecast = list.getJSONObject(i)
            val dateTime = forecast.getString("dt_txt")
            // "2024-08-23 09:00:00" の形式で、日付が一致するか確認
            if (dateTime.startsWith(targetDate)) {
                forecastsForTomorrow.add(forecast)
            }
        }

        if (forecastsForTomorrow.isNotEmpty()) {
            val forecastDescriptions = forecastsForTomorrow.joinToString("\n") { forecast ->
                val time = forecast.getString("dt_txt").substring(11, 16) // "HH:mm"形式
                val weatherArray = forecast.getJSONArray("weather")
                val weather = weatherArray.getJSONObject(0)
                val description = weather.getString("description")
                "$time の天気は ${description}"
            }
            binding.tvWeatherTelop.text = "${cityName}の明日の天気予報"
            binding.tvWeatherDesc.text = forecastDescriptions
        } else {
            binding.tvWeatherTelop.text = "天気予報が見つかりませんでした"
            binding.tvWeatherDesc.text = ""
        }
    }

    private class WeatherInfoBackgroundReceiver(private val urlString: String) : Callable<String> {
        @WorkerThread
        override fun call(): String {
            val url = URL(urlString)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
                requestMethod = "GET"
            }
            return try {
                conn.connect()
                val result = conn.inputStream.reader().readText()
                result
            } catch (ex: SocketTimeoutException) {
                Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                ""
            } finally {
                conn.disconnect()
            }
        }
    }
}
