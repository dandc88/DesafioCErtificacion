package com.desafiocertificacion.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desafiocertificacion.weatherapp.R
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.databinding.ItemCityBinding
import java.util.Locale


class WeatherListAdapter(private val onCityClick: (CityEntity, Int) -> Unit) :
    ListAdapter<CityEntity, WeatherListAdapter.WeatherViewHolder>(CityDiffCallback()) {

    private var currentPreferences: PreferencesEntity? = null

    // Establecer las preferencias desde el fragmento
    fun setPreferences(newPreferences: PreferencesEntity) {
        if (currentPreferences != newPreferences) {
            val oldPreferences = currentPreferences
            currentPreferences = newPreferences

            // Actualiza solo los ítems afectados por el cambio de preferencias
            for (i in 0 until itemCount) {
                if (hasPreferencesChangedForCity(oldPreferences, newPreferences)) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding, onCityClick, currentPreferences)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    // Verifica si las preferencias han cambiado para un ítem específico
    private fun hasPreferencesChangedForCity(
        oldPreferences: PreferencesEntity?,
        newPreferences: PreferencesEntity
    ): Boolean {
        if (oldPreferences == null) return true
        return oldPreferences.temperatureUnit != newPreferences.temperatureUnit ||
                oldPreferences.windSpeedUnit != newPreferences.windSpeedUnit
    }

    class WeatherViewHolder(
        private val binding: ItemCityBinding,
        private val onCityClick: (CityEntity, Int) -> Unit,
        private val preferences: PreferencesEntity?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: CityEntity, position: Int) {
            binding.tvCountry.text = city.country
            binding.tvCity.text = city.name

            // Determinar la unidad de temperatura según las preferencias
            val temperatureUnitSymbol = when (preferences?.temperatureUnit) {
                "Celsius" -> "C"
                "Fahrenheit" -> "F"
                else -> "C"
            }

            // Convertir la temperatura dependiendo de las preferencias
            val temperature = if (temperatureUnitSymbol == "C") {
                city.temperature // Ya en Celsius
            } else {
                (city.temperature * 9 / 5) + 32 // Conversión a Fahrenheit
            }

            // Mostrar la temperatura con la unidad adecuada
            binding.tvTemperature.text = binding.root.context.getString(R.string.temperature, temperature, temperatureUnitSymbol)
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


