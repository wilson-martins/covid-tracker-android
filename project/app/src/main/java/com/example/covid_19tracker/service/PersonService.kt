package com.example.covid_19tracker.service

import com.example.covid_19tracker.common.Constants
import com.example.covid_19tracker.model.Person
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT


interface PersonService {

    @PUT("person")
    fun signUpPerson(@Body person: Person): Call<Person>

    companion object Factory {
        fun create(): PersonService {
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
            val httpClient = OkHttpClient.Builder().addInterceptor(logging)

            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create()

            val builder = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))

            return builder
                .client(httpClient.build())
                .build().create(PersonService::class.java)
        }
    }
}