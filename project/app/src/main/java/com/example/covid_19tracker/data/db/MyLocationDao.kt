package com.example.covid_19tracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface MyLocationDao {

    @Query("SELECT * FROM my_location_table ORDER BY date DESC")
    fun getLocations(): LiveData<List<MyLocationEntity>>

    @Query("SELECT * FROM my_location_table where count > 20 and date > datetime('now', '-14 days') ORDER BY date DESC")
    fun getMainLocations(): LiveData<List<MyLocationEntity>>

    @Query("SELECT * FROM my_location_table WHERE id=(:id)")
    fun getLocation(id: UUID): LiveData<MyLocationEntity>

    @Query("SELECT * FROM my_location_table WHERE latitude=(:latitude) and longitude=(:longitude)")
    fun getByLatLng(latitude: Double, longitude: Double): LiveData<MyLocationEntity>

    @Update
    fun updateLocation(myLocationEntity: MyLocationEntity)

    @Insert
    fun addLocation(myLocationEntity: MyLocationEntity)

    @Insert
    fun addLocations(myLocationEntities: List<MyLocationEntity>)
}