package com.example.covid_19tracker.model

data class Person(var personId: Long? = null,
                  var firstName: String = "",
                  var lastName: String = "",
                  var emailAddr: String = "",
                  var birthYear: String = "",
                  var googleId: String = "",
                  var googleIdToken: String = "",
                  var googleProfilePictureUrl: String = "")