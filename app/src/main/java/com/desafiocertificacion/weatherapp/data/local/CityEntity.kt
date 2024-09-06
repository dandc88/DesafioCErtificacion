package com.desafiocertificacion.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String,
    val temperature: Double,
    val weatherDescription: String,
    val weatherIcon: String
)