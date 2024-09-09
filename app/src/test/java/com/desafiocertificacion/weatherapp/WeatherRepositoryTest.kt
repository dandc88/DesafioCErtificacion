package com.desafiocertificacion.weatherapp

import app.cash.turbine.test
import com.desafiocertificacion.weatherapp.data.WeatherRepository
import com.desafiocertificacion.weatherapp.data.WeatherRepositoryImpl
import com.desafiocertificacion.weatherapp.data.local.CityDao
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesDao
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class WeatherRepositoryTest {

    private val mockPreferencesDao: PreferencesDao = mock()
    private val mockCityDao: CityDao = mock()
    private val repository: WeatherRepository = WeatherRepositoryImpl(mockCityDao, mockPreferencesDao)

    @Test
    fun getPreferences() = runTest {
        // Simulación de preferencias
        val preferences = PreferencesEntity(temperatureUnit = "Celsius", windSpeedUnit = "m/s")

        // Simulación del retorno de Flow desde el DAO
        `when`(mockPreferencesDao.getPreferences()).thenReturn(flowOf(preferences))

        // Verificación del comportamiento
        repository.getPreferences().test {
            val emittedPreferences = awaitItem()
            assertEquals("Celsius", emittedPreferences.temperatureUnit)
            assertEquals("m/s", emittedPreferences.windSpeedUnit)
            awaitComplete()
        }

        verify(mockPreferencesDao, times(1)).getPreferences()
    }

    @Test
    fun `getPreferences should handle errors`() = runTest {
        // Simulación de error
        `when`(mockPreferencesDao.getPreferences()).thenThrow(RuntimeException("Database error"))

        try {
            repository.getPreferences().test {
                awaitComplete()
            }
        } catch (e: Exception) {
            assertEquals("Database error", e.message)
        }

        verify(mockPreferencesDao, times(1)).getPreferences()
    }

    @Test
    fun `repository should combine data from multiple sources`() = runTest {
        // Simulación de múltiples fuentes de datos
        val preferences = PreferencesEntity(temperatureUnit = "Celsius", windSpeedUnit = "m/s")
        val cities = listOf(CityEntity(name = "Santiago", country = "Chile", temperature = 25.0, weatherDescription = "Sunny", weatherIcon = "01d"))

        `when`(mockPreferencesDao.getPreferences()).thenReturn(flowOf(preferences))
        `when`(mockCityDao.getAllCities()).thenReturn(flowOf(cities))

        // Verifica que ambas fuentes interactúan correctamente
        repository.getPreferences().test {
            val emittedPreferences = awaitItem()
            assertEquals("Celsius", emittedPreferences.temperatureUnit)
            awaitComplete()
        }

        repository.getAllCities().test {
            val emittedCities = awaitItem()
            assertEquals(1, emittedCities.size)
            assertEquals("Santiago", emittedCities[0].name)
            awaitComplete()
        }
    }
}
