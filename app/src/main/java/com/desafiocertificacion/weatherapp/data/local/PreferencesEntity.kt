package com.desafiocertificacion.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val temperatureUnit: String,
    val windSpeedUnit: String
)