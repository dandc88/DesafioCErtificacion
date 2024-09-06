package com.desafiocertificacion.weatherapp

import android.app.Application
import android.util.Log
import com.desafiocertificacion.weatherapp.data.WeatherRepositoryImpl
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.WeatherDatabase
import com.desafiocertificacion.weatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherApp : Application() {

    val database by lazy { WeatherDatabase.getDatabase(this) }
    val repository by lazy { WeatherRepositoryImpl(database.cityDao(), database.preferencesDao()) }

    override fun onCreate() {
        super.onCreate()
        // Llama a fetchWeatherData solo al iniciar la aplicación
        Log.i("WeatherApp", "Application started")
        println("Se creo")
        CoroutineScope(Dispatchers.IO).launch {
            println("llamará a fetchWeatherData ")
            fetchWeatherData()
            println("llamó a fetchWeatherData ")

        }
    }

    private suspend fun fetchWeatherData() {
        println("Respondió 1 fetchWeatherData ")
        try {
            // Eliminar datos antiguos
            repository.deleteAllCities()

            // Obtener los datos de la API
            val response = RetrofitInstance.api.getWeatherData()
            println("Respondió fetchWeatherData ")
            Log.d("WeatherApp", "API Response: $response")

            // Mapear la respuesta de la API a entidades de base de datos
            val cityEntities = response.data.map { cityDto ->
                CityEntity(
                    id = 0, // Se ajustará automáticamente si se usa UPsert
                    name = cityDto.name,
                    country = cityDto.sys.country,
                    temperature = cityDto.main.temp,
                    weatherDescription = cityDto.weather[0].description,
                    weatherIcon = cityDto.weather[0].icon
                )
            }

            Log.d("WeatherApp", "Mapped Cities: $cityEntities")

            // Insertar/Actualizar los datos en la base de datos
            repository.upsertCities(cityEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}