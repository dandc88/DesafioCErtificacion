package com.desafiocertificacion.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: PreferencesEntity)

    @Query("SELECT * FROM preferences LIMIT 1")
    fun getPreferences(): Flow<PreferencesEntity>
}
