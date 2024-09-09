package com.desafiocertificacion.weatherapp

import android.app.Application
import android.util.Log
import com.desafiocertificacion.weatherapp.data.WeatherRepositoryImpl
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.data.local.WeatherDatabase
import com.desafiocertificacion.weatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WeatherApp : Application() {

    val database by lazy { WeatherDatabase.getDatabase(this) }
    val repository by lazy { WeatherRepositoryImpl(database.cityDao(), database.preferencesDao()) }

    override fun onCreate() {
        super.onCreate()
        Log.i("WeatherApp", "Application started")

        // Ejecutar en IO ya que estamos realizando operaciones en la base de datos y red
        CoroutineScope(Dispatchers.IO).launch {
            initializePreferences() // Inicializa preferencias
            fetchWeatherData() // Llamar una vez para obtener datos
        }
    }

    private suspend fun initializePreferences() {
        val currentPreferences = repository.getPreferences().firstOrNull()
        if (currentPreferences == null) {
            repository.insertPreferences(PreferencesEntity(temperatureUnit = "Celsius", windSpeedUnit = "m/s"))
        }
    }

    private suspend fun fetchWeatherData() {
        try {
            val response = RetrofitInstance.api.getWeatherData()
            Log.d("WeatherApp", "API Response: $response")

            // Obtener las ciudades almacenadas en la base de datos
            val existingCities = repository.getAllCities().firstOrNull() ?: emptyList()

            // Mapeo de las ciudades obtenidas de la API
            val cityEntities = response.data.map { cityDto ->
                CityEntity(
                    id = 0, // Se ajustará en el upsert
                    name = cityDto.name,
                    country = cityDto.sys.country,
                    temperature = cityDto.main.temp,
                    weatherDescription = cityDto.weather[0].description,
                    weatherIcon = cityDto.weather[0].icon
                )
            }

            // Actualizar ciudades solo si ha habido cambios
            val citiesToUpsert = cityEntities.map { newCity ->
                existingCities.find { it.name == newCity.name }?.let { existingCity ->
                    // Actualiza solo si la ciudad ha cambiado
                    if (existingCity.temperature != newCity.temperature ||
                        existingCity.weatherDescription != newCity.weatherDescription ||
                        existingCity.weatherIcon != newCity.weatherIcon) {

                        existingCity.copy(
                            temperature = newCity.temperature,
                            weatherDescription = newCity.weatherDescription,
                            weatherIcon = newCity.weatherIcon
                        )
                    } else {
                        existingCity
                    }
                } ?: newCity // Si no existe la ciudad, se inserta
            }

            // Solo realiza la inserción si hay cambios
            if (citiesToUpsert != existingCities) {
                Log.d("WeatherApp", "Cities to upsert: $citiesToUpsert")
                repository.upsertCities(citiesToUpsert)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
