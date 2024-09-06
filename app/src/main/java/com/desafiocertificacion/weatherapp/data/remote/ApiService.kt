package com.desafiocertificacion.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class WeatherResponse(val data: List<CityDto>)

// Modelo para cada ciudad en la respuesta
data class CityDto(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

// Modelos adicionales
data class Coord(val lon: Double, val lat: Double)
data class Weather(val id: Int, val main: String, val description: String, val icon: String)
data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int, val sea_level: Int, val grnd_level: Int)
data class Wind(val speed: Double, val deg: Double, val gust: Double? = null)
data class Rain(val `1h`: Double? = null)
data class Clouds(val all: Int)
data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Int, val sunset: Int)

interface ApiService {

    // Obtener la lista de todas las ciudades
    @GET("/")
    suspend fun getWeatherData(): WeatherResponse


    // Obtener la lista de todas las ciudades
    @GET("/data/{id}")
    suspend fun getWeatherDataForCity(@Path("id") cityId: Int): CityDto
}