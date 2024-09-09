package com.desafiocertificacion.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.desafiocertificacion.weatherapp.WeatherApp
import com.desafiocertificacion.weatherapp.data.local.PreferencesEntity
import com.desafiocertificacion.weatherapp.databinding.FragmentSettingsBinding
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModel
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    // Inicializar el ViewModel usando la fábrica personalizada
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((requireContext().applicationContext as WeatherApp).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar las preferencias actuales al abrir el fragmento
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.preferences.collect { preferences ->
                    preferences?.let {
                        // Actualiza los RadioButtons según las preferencias
                        when (it.temperatureUnit) {
                            "Celsius" -> binding.rbCelsius.isChecked = true
                            "Fahrenheit" -> binding.rbFahrenheit.isChecked = true
                        }

                        when (it.windSpeedUnit) {
                            "m/s" -> binding.rbMps.isChecked = true
                            "mph" -> binding.rbMph.isChecked = true
                        }
                    }
                }
            }
        }

        // Acción del botón de retroceso
        binding.btnBack.setOnClickListener {
            savePreferences() // Guardar las preferencias antes de volver
            findNavController().navigateUp() // Volver al fragmento anterior
        }
    }

    private fun savePreferences() {
        // Obtener las preferencias seleccionadas
        val selectedTemperatureUnit = if (binding.rbCelsius.isChecked) "Celsius" else "Fahrenheit"
        val selectedWindSpeedUnit = if (binding.rbMps.isChecked) "m/s" else "mph"

        // Crear un objeto de PreferencesEntity con las selecciones
        val newPreferences = PreferencesEntity(
            id = 1, // Usar siempre el ID 1 para asegurarse de que es una sola fila
            temperatureUnit = selectedTemperatureUnit,
            windSpeedUnit = selectedWindSpeedUnit
        )

        // Guardar las preferencias en la base de datos
        viewModel.insertPreferences(newPreferences)
    }
}


