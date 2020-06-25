package com.example.covid_19tracker.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PersonServiceFactory {

    private const val API_BASE_URL = "http://10.0.2.2:8080/"

    // add the logging interceptor

    fun makeService(): PersonService {
        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging)

        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        return builder
            .client(httpClient.build())
            .build().create(PersonService::class.java)
    }
}