package com.example.covid_19tracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName = "my_location_table")
data class MyLocationEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val foreground: Boolean = true,
    val date: Date = Date()
) {

    override fun toString(): String {
        val appState = if (foreground) {
            "in app"
        } else {
            "in BG"
        }

        return "$latitude, $longitude $appState on " +
                "${DateFormat.getDateTimeInstance().format(date)}.\n"
    }
}