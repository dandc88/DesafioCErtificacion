package com.desafiocertificacion.weatherapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.desafiocertificacion.weatherapp.R
import com.desafiocertificacion.weatherapp.WeatherApp
import com.desafiocertificacion.weatherapp.data.remote.CityDto
import com.desafiocertificacion.weatherapp.databinding.FragmentWeatherDetailBinding
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModel
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModelFactory
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)

        val cityId = args.cityId
        Log.d("WeatherDetailFragment", "City ID: $cityId") // Log para verificar el ID

        viewModel.fetchWeatherDetail(cityId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cityWeatherDetail.collect { cityDetail ->
                if (cityDetail != null) {
                    Log.d("WeatherDetailFragment", "CityDetail: $cityDetail") // Log para verificar los detalles
                    updateUI(cityDetail)
                }else{
                    Log.d("WeatherDetailFragment", "CityDetail is null")
                }
            }
        }

        return binding.root
    }

    private fun updateUI(cityDetail: CityDto) {
        Log.d("WeatherDetailFragment", "Updating UI with CityDetail: $cityDetail")
        binding.cityName.text = cityDetail.name
        binding.currentTemperature.text = "${cityDetail.main.temp}°"
        binding.weatherIcon.setImageResource(getWeatherIcon(cityDetail.weather[0].icon))
        binding.minTemperature.text = "Min ${cityDetail.main.temp_min}°"
        binding.maxTemperature.text = "Max ${cityDetail.main.temp_max}°"
        binding.humidity.text = "Humidity ${cityDetail.main.humidity}%"
        binding.pressure.text = "Pressure ${cityDetail.main.pressure} hPa"
        binding.windSpeed.text = "Speed ${cityDetail.wind.speed} m/s"
        binding.windDirection.text = "Direction ${getWindDirection(cityDetail.wind.deg)}"  // Cambio aquí
        binding.sunriseTime.text = "Sunrise ${convertTime(cityDetail.sys.sunrise, cityDetail.timezone)}"
        binding.sunsetTime.text = "Sunset ${convertTime(cityDetail.sys.sunset, cityDetail.timezone)}"
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
            else -> null // No se asigna ninguna imagen
        }

        if (backgroundResId != null) {
            binding.cityBackground.setImageResource(backgroundResId)
        } else {
            binding.cityBackground.setImageDrawable(null) // Limpia cualquier imagen establecida
            binding.cityBackground.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white)) // Establece el color blanco
        }
    }
}

