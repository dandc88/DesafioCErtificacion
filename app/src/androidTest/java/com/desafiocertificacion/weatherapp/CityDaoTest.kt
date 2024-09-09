package com.desafiocertificacion.weatherapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.desafiocertificacion.weatherapp.data.local.CityDao
import com.desafiocertificacion.weatherapp.data.local.CityEntity
import com.desafiocertificacion.weatherapp.data.local.WeatherDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first

@RunWith(AndroidJUnit4::class)
@SmallTest
class CityDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var cityDao: CityDao

    @Before
    fun setup() {
        // Utiliza una base de datos en memoria para pruebas
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        cityDao = database.cityDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetCities() = runBlocking {
        val city = CityEntity(name = "Santiago", country = "Chile", temperature = 25.0, weatherDescription = "Sunny", weatherIcon = "01d")
        cityDao.upsertCities(listOf(city))

        val cities = cityDao.getAllCities().first()

        assertEquals(1, cities.size)
        assertEquals("Santiago", cities[0].name)
    }

    @Test
    fun deleteAllCities() = runBlocking {
        val city = CityEntity(name = "Reykjavik", country = "Iceland", temperature = 10.0, weatherDescription = "Cloudy", weatherIcon = "04d")
        cityDao.upsertCities(listOf(city))
        cityDao.deleteAllCities()

        val cities = cityDao.getAllCities().first()
        assertEquals(0, cities.size)
    }
}
