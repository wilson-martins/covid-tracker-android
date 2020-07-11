package com.example.covid_19tracker.model

import com.example.covid_19tracker.common.Constants
import java.util.*

data class StatusHistory constructor(var statusHistoryId: Long? = null,
                                     var diseaseId: Int = Constants.DISEASE_ID,
                                     var statusDt: Date = Date(),
                                     var value: HealthState,
                                     var personId: Long)