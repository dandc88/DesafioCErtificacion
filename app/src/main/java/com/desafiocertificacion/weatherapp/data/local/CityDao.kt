package com.desafiocertificacion.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCities(cities: List<CityEntity>)

    @Query("SELECT * FROM city")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("DELETE FROM city")
    fun deleteAllCities(): Int


}
