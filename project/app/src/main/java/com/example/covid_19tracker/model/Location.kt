package com.example.covid_19tracker.model

import java.util.*

data class Location(
    var personId: Long? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var visitedDt: Date?
)