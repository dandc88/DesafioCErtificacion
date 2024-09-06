package com.desafiocertificacion.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desafiocertificacion.weatherapp.R
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.databinding.ItemCityBinding
import java.util.Locale


class WeatherListAdapter(private val onCityClick: (CityEntity, Int) -> Unit) :
    ListAdapter<CityEntity, WeatherListAdapter.WeatherViewHolder>(CityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding, onCityClick)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class WeatherViewHolder(
        private val binding: ItemCityBinding,
        private val onCityClick: (CityEntity, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: CityEntity, position: Int) {
            binding.tvCountry.text = city.country
            binding.tvCity.text = city.name
            binding.tvTemperature.text = "${city.temperature}°C"
            binding.tvWeatherDesc.text = city.weatherDescription

            // Asignar ícono según la descripción del clima
            val weatherIcon = when (city.weatherDescription.lowercase(Locale.ROOT)) {
                "clear sky" -> R.drawable.sunny
                "partly cloudy" -> R.drawable.partly_cloudy
                "light rain" -> R.drawable.light_rain
                "rain" -> R.drawable.rain
                "overcast clouds" -> R.drawable.cloudy
                else -> R.drawable.ic_sunny // icono por defecto
            }
            binding.ivWeatherIcon.setImageResource(weatherIcon)

            // Configurar clic en el item
            binding.root.setOnClickListener {
                onCityClick(city, position)
            }
        }
    }

    class CityDiffCallback : DiffUtil.ItemCallback<CityEntity>() {
        override fun areItemsTheSame(oldItem: CityEntity, newItem: CityEntity): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CityEntity, newItem: CityEntity): Boolean {
            return oldItem == newItem
        }
    }
}
