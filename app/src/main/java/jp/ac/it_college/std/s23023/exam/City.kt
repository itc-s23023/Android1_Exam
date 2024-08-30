package jp.ac.it_college.std.s23023.exam

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import java.util.Locale

data class City(
    val name: String,
    val q: String
) {
    companion object {
        val list: List<City>
            get() {
                val locales = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                return if (locales.get(0)?.language == Locale.JAPAN.language) {
                    listOf(
                        City("大阪", "Osaka"),
                        City("神戸", "Kobe"),
                        City("京都", "Kyoto"),
                        City("大津", "Otsu"),
                        City("奈良", "Nara"),
                        City("和歌山", "Wakayama"),
                        City("姫路", "Himeji"),
                        City("那覇", "Naha"),
                    )
                } else {
                    listOf(
                        City("Osaka", "Osaka"),
                        City("Kobe", "Kobe"),
                        City("Kyoto", "Kyoto"),
                        City("Otsu", "Otsu"),
                        City("Nara", "Nara"),
                        City("Wakayama", "Wakayama"),
                        City("Himeji", "Himeji"),
                        City("Naha", "Naha"),
                    )
                }
            }
    }
}