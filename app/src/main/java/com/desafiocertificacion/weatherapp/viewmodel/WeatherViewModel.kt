package com.desafiocertificacion.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.desafiocertificacion.weatherapp.data.WeatherRepository
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.data.remote.CityDto
import com.desafiocertificacion.weatherapp.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    // Flujos que emiten los datos de las ciudades y las preferencias
    val allCities: StateFlow<List<CityEntity>> = repository.getAllCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val preferences: StateFlow<PreferencesEntity?> = repository.getPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Nueva función para obtener el clima detallado de una ciudad por ID
    private val _cityWeatherDetail = MutableStateFlow<CityDto?>(null)
    val cityWeatherDetail: StateFlow<CityDto?> get() = _cityWeatherDetail

    // Insertar las preferencias en la base de datos
    fun insertPreferences(preferences: PreferencesEntity) = viewModelScope.launch {
        repository.insertPreferences(preferences)
    }

    // Eliminar todas las ciudades
    fun deleteAllCities() = viewModelScope.launch {
        repository.deleteAllCities()
    }
    fun fetchWeatherDetail(cityId: Int) = viewModelScope.launch {
        try {
            // Reemplaza el ID en el endpoint
            val response = RetrofitInstance.api.getWeatherDataForCity(cityId)
            _cityWeatherDetail.value = response
        } catch (e: Exception) {
            e.printStackTrace() // Maneja errores
        }
    }


}

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

