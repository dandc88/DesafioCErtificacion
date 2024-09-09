package com.desafiocertificacion.weatherapp

import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.desafiocertificacion.weatherapp.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class WeatherListFragmentTest {

    
    @Test
    fun testNavigationToSettings() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Simular clic en el botón de configuración
        onView(withId(R.id.btn_settings)).perform(click())

        // Esperar 2 segundos usando el IdlingResource personalizado
        val idlingResource = ElapsedTimeIdlingResource(2000)
        IdlingRegistry.getInstance().register(idlingResource)

        // Verificar que el NavController ha navegado al fragmento de configuración
        activityScenario.onActivity { activity ->
            val navController = Navigation.findNavController(activity.findViewById(R.id.nav_host_fragment))
            val currentDestinationId = navController.currentDestination?.id

            assertEquals(R.id.settingsFragment, currentDestinationId)
        }

        // Desregistrar el IdlingResource para limpiar
        IdlingRegistry.getInstance().unregister(idlingResource)
    }




}



