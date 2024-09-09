package com.desafiocertificacion.weatherapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.desafiocertificacion.weatherapp.R
import com.desafiocertificacion.weatherapp.WeatherApp
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.data.remote.CityDto
import com.desafiocertificacion.weatherapp.databinding.FragmentWeatherDetailBinding
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModel
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class WeatherDetailFragment : Fragment() {

    private lateinit var binding: FragmentWeatherDetailBinding
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((requireContext().applicationContext as WeatherApp).repository)
    }
    private val args: WeatherDetailFragmentArgs by navArgs()

    private var isDataLoaded = false // Bandera para cargar los datos solo una vez

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)

        val cityId = args.cityId


        if (!isDataLoaded) {
            // Obtener detalles del clima de la ciudad solo si no se ha cargado antes
            viewModel.fetchWeatherDetail(cityId)
            isDataLoaded = true // Marcar como cargado
        }



        // Observar tanto las preferencias como los detalles del clima
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observePreferences() }
            launch { observeCityWeatherDetails() }
        }

        return binding.root
    }

    // Recolecta las preferencias del usuario
    private suspend fun observePreferences() {
        viewModel.preferences.collectLatest { preferences ->
            preferences?.let {

            }
        }
    }

    // Recolecta los detalles del clima de la ciudad
    private suspend fun observeCityWeatherDetails() {
        viewModel.cityWeatherDetail.collectLatest { cityDetail ->
            cityDetail?.let {

                viewModel.preferences.value?.let { preferences ->
                    updateUI(cityDetail, preferences)
                    setupMapClickListener(cityDetail)
                }
            } ?: Log.d("WeatherDetailFragment", "CityDetail is null")
        }
    }

    private fun setupMapClickListener(cityDetail: CityDto) {
        binding.mapIcon.setOnClickListener {
            // Crear la URI utilizando el esquema geo: con coordenadas
            val gmmIntentUri = Uri.parse("geo:${cityDetail.coord.lat},${cityDetail.coord.lon}?q=${cityDetail.name}")

            // Crear el Intent para abrir la URI en una aplicación de mapas
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            // Lanzar el Intent directamente
            startActivity(mapIntent)
        }
    }





    // Actualiza la UI con los detalles del clima y las preferencias del usuario
    private fun updateUI(cityDetail: CityDto, preferences: PreferencesEntity) {
        val temperatureUnitSymbol = when (preferences.temperatureUnit) {
            "Celsius" -> "C"
            "Fahrenheit" -> "F"
            else -> "C"
        }

        val temperature = viewModel.convertTemperature(cityDetail.main.temp, preferences.temperatureUnit)
        val windSpeed = viewModel.convertWindSpeed(cityDetail.wind.speed, preferences.windSpeedUnit)

        binding.cityName.text = cityDetail.name
        binding.currentTemperature.text = getString(R.string.temperature, temperature, temperatureUnitSymbol)
        binding.weatherIcon.setImageResource(getWeatherIcon(cityDetail.weather[0].icon))
        binding.minTemperature.text = getString(R.string.min_temperature, viewModel.convertTemperature(cityDetail.main.temp_min, preferences.temperatureUnit))
        binding.maxTemperature.text = getString(R.string.max_temperature, viewModel.convertTemperature(cityDetail.main.temp_max, preferences.temperatureUnit))
        binding.humidity.text = getString(R.string.humidity, cityDetail.main.humidity)
        binding.pressure.text = getString(R.string.pressure, cityDetail.main.pressure)
        binding.windSpeed.text = getString(R.string.wind_speed, windSpeed, preferences.windSpeedUnit)
        binding.windDirection.text = getString(R.string.wind_direction, getWindDirection(cityDetail.wind.deg))
        binding.sunriseTime.text = getString(R.string.sunrise, convertTime(cityDetail.sys.sunrise, cityDetail.timezone))
        binding.sunsetTime.text = getString(R.string.sunset, convertTime(cityDetail.sys.sunset, cityDetail.timezone))

        updateBackground(cityDetail.name)
    }

    private fun getWeatherIcon(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.sunny
            "04d" -> R.drawable.cloudy
            "10n" -> R.drawable.light_rain
            else -> R.drawable.ic_sunny
        }
    }

    private fun getWindDirection(degrees: Double): String {
        return when {
            degrees < 22.5 || degrees >= 337.5 -> "N"
            degrees < 67.5 -> "NE"
            degrees < 112.5 -> "E"
            degrees < 157.5 -> "SE"
            degrees < 202.5 -> "S"
            degrees < 247.5 -> "SW"
            degrees < 292.5 -> "W"
            else -> "NW"
        }
    }

    private fun convertTime(unixTime: Int, timeZoneOffset: Int): String {
        val date = Date(unixTime * 1000L)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeZone = TimeZone.getTimeZone("GMT${if (timeZoneOffset >= 0) "+" else "-"}${Math.abs(timeZoneOffset) / 3600}")
        sdf.timeZone = timeZone
        return sdf.format(date)
    }

    private fun updateBackground(cityName: String) {
        val backgroundResId = when (cityName) {
            "Santiago" -> R.drawable.santiago_de_chile
            "Thimphu" -> R.drawable.thimphu_bhutan
            "Reykjavik" -> R.drawable.reykjavik_iceland
            else -> null
        }

        if (backgroundResId != null) {
            binding.cityBackground.setImageResource(backgroundResId)
        } else {
            binding.cityBackground.setImageDrawable(null)
            binding.cityBackground.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }
}


