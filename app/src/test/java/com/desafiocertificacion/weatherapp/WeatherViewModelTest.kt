package com.desafiocertificacion.weatherapp

import app.cash.turbine.test
import com.desafiocertificacion.weatherapp.data.WeatherRepository
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class WeatherViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private val mockRepository: WeatherRepository = mock()

    @Test
    fun `preferences should be observed and updated in ViewModel`() = runTest {
        // Simular retorno de preferencias desde el repositorio
        val preferences = PreferencesEntity(temperatureUnit = "Celsius", windSpeedUnit = "m/s")
        `when`(mockRepository.getPreferences()).thenReturn(flowOf(preferences))

        val viewModel = WeatherViewModel(mockRepository)

        // Verificar que el ViewModel emita correctamente las preferencias
        viewModel.preferences.test {
            // Captura el primer ítem emitido
            val emittedPreferences = awaitItem()
            if (emittedPreferences != null) {
                assertEquals("Celsius", emittedPreferences.temperatureUnit)
            }
            if (emittedPreferences != null) {
                assertEquals("m/s", emittedPreferences.windSpeedUnit)
            }

            // Ignorar cualquier otro evento y finalizar el test
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ViewModel should correctly convert temperature and wind speed`() = runTest {
        // Crear el ViewModel con un mock de WeatherRepository
        val mockRepository = mock(WeatherRepository::class.java)
        val viewModel = WeatherViewModel(mockRepository)

        // Definir temperaturas y velocidades a convertir
        val tempInCelsius = 25.0
        val speedInMetersPerSecond = 10.0

        // Convertir la temperatura de Celsius a Fahrenheit
        val convertedTemp = viewModel.convertTemperature(tempInCelsius, "Fahrenheit")
        assertEquals(77.0, convertedTemp, 0.1) // 25°C * 9/5 + 32 = 77°F

        // Convertir la velocidad del viento de m/s a mph
        val convertedSpeed = viewModel.convertWindSpeed(speedInMetersPerSecond, "mph")
        assertEquals(22.37, convertedSpeed, 0.1) // 10 m/s * 2.237 = 22.37 mph
    }


}
