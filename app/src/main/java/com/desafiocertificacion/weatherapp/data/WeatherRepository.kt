package com.desafiocertificacion.weatherapp.data

import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // City-related operations
    fun getAllCities(): Flow<List<CityEntity>>
    // Método para insertar o actualizar una lista de ciudades
    suspend fun upsertCities(cities: List<CityEntity>)
    suspend fun deleteAllCities()

    // Preferences-related operations
    fun getPreferences(): Flow<PreferencesEntity>
    suspend fun insertPreferences(preferences: PreferencesEntity)


}
