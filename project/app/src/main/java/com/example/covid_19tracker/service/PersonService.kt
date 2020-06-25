package com.example.covid_19tracker.service

import com.example.covid_19tracker.model.Person
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface PersonService {

    @PUT("person")
    fun signUpPerson(@Body person: Person): Call<Person>
}