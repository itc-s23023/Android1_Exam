package jp.ac.it_college.std.s23023.exam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.ac.it_college.std.s23023.exam.databinding.CityRowBinding

class CityListAdapter(
    private val data: List<City>,
    private val onItemSelected: (City) -> Unit = {}
) : RecyclerView.Adapter<CityListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: CityRowBinding) : RecyclerView.ViewHolder(binding.root) {
        var onItemClick: (City) -> Unit = {}
        fun bind(item: City) {
            binding.apply {
                cityName.text = item.name
                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(CityRowBinding.inflate(inflater, parent, false))
    }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onItemClick = onItemSelected
        holder.bind(data[position])
    }
}