package com.example.covid_19tracker.service

import com.example.covid_19tracker.common.Constants
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.model.StatusHistory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT

interface StatusHistoryService {

    @PUT("statusHistory")
    fun addStatusHistory(@Body statusHistory: StatusHistory): Call<StatusHistory>

    companion object Factory {
        fun create(): StatusHistoryService {
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
            val httpClient = OkHttpClient.Builder().addInterceptor(logging)

            val builder = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

            return builder
                .client(httpClient.build())
                .build().create(StatusHistoryService::class.java)
        }
    }
}