package com.desafiocertificacion.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafiocertificacion.weatherapp.R
import com.desafiocertificacion.weatherapp.WeatherApp
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.databinding.FragmentWeatherListBinding
import com.desafiocertificacion.weatherapp.ui.adapter.WeatherListAdapter
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModel
import com.desafiocertificacion.weatherapp.viewmodel.WeatherViewModelFactory

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WeatherListFragment : Fragment() {

    private var _binding: FragmentWeatherListBinding? = null
    private val binding get() = _binding!!

    // Obtener el ViewModel utilizando la fábrica de ViewModels
    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((requireActivity().application as WeatherApp).repository)
    }

    private lateinit var weatherListAdapter: WeatherListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicializar View Binding
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()

        // Observar los datos del ViewModel
        observeWeatherData()



        // Configurar acción del botón de configuración
        binding.btnSettings.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_weatherListFragment_to_settingsFragment)
        }

    }

    // Configurar la toolbar
    private fun setupToolbar() {
        binding.toolbarTitle.text = getString(R.string.app_name)
    }

    // Configurar RecyclerView con el adaptador
    private fun setupRecyclerView() {
        // Pasar el listener al adaptador
        weatherListAdapter = WeatherListAdapter { city, position ->
            // Navegar al fragmento de detalles con el id de la ciudad
            val action = WeatherListFragmentDirections.actionWeatherListFragmentToWeatherDetailFragment(position)
            findNavController().navigate(action)
        }

        binding.rvWeather.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = weatherListAdapter
        }
    }

    // Observar los cambios de los datos en el ViewModel y actualizar el RecyclerView
    private fun observeWeatherData() {
        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.allCities.collect { cityList ->
                updateRecyclerView(cityList)
            }
        }
    }

    // Actualizar el adaptador con la nueva lista de ciudades
    private fun updateRecyclerView(cities: List<CityEntity>) {
        weatherListAdapter.submitList(cities)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}