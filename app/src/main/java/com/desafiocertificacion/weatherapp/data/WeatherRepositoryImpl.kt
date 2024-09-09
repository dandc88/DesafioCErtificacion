package com.desafiocertificacion.weatherapp.data

import com.desafiocertificacion.weatherapp.data.local.CityDao
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesDao
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val cityDao: CityDao,
    private val preferencesDao: PreferencesDao
) : WeatherRepository {

    // City-related operations
    override fun getAllCities(): Flow<List<CityEntity>> = cityDao.getAllCities()

    override suspend fun upsertCities(cities: List<CityEntity>) {
        cityDao.upsertCities(cities)
    }

    override suspend fun deleteAllCities() {
        cityDao.deleteAllCities()
    }

    // Preferences-related operations
    override fun getPreferences(): Flow<PreferencesEntity> = preferencesDao.getPreferences()

    override suspend fun insertPreferences(preferences: PreferencesEntity) {
        preferencesDao.insertPreferences(preferences)
    }


}
