package com.example.covid_19tracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.service.PersonService
import com.example.covid_19tracker.service.PersonServiceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(){

    // Control variables
    var validForm = false

    // Layout components
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var birthYearEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var zipCodeEditText: EditText
    private lateinit var addressTypeEditText: EditText
    private lateinit var signUpButton: Button

    private lateinit var personService: PersonService
    // Text instructions guide the user when inputing data
    private var oldTextLabels: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize components
        firstNameEditText = findViewById(R.id.edit_first_name)
        lastNameEditText = findViewById(R.id.edit_last_name)
        emailEditText = findViewById(R.id.edit_email)
        birthYearEditText = findViewById(R.id.edit_birth_year)
        countryEditText = findViewById(R.id.edit_contry)
        stateEditText = findViewById(R.id.edit_state)
        cityEditText = findViewById(R.id.edit_city)
        zipCodeEditText = findViewById(R.id.edit_zip_code)
        addressTypeEditText = findViewById(R.id.edit_type_address)
        signUpButton = findViewById(R.id.signup_button)

        // Get data from google account
        firstNameEditText.setText(SharedPreferencesSettings.loadString(this, SharedPreferenceKeys.GOOGLE_FIRST_NAME))
        lastNameEditText.setText(SharedPreferencesSettings.loadString(this,SharedPreferenceKeys.GOOGLE_LAST_NAME))
        emailEditText.setText(SharedPreferencesSettings.loadString(this, SharedPreferenceKeys.GOOGLE_EMAIL))

        // Add change listeners to validate the form
        val message = "Este campo deve ser preeenchido!"
        birthYearEditText.validate(message){s -> !s.isNullOrBlank()}
        countryEditText.validate(message){s -> !s.isNullOrBlank()}
        stateEditText.validate(message){s -> !s.isNullOrBlank()}
        cityEditText.validate(message){s -> !s.isNullOrBlank()}
        zipCodeEditText.validate(message){s -> !s.isNullOrBlank()}
        zipCodeEditText.validate(message){s -> !s.isNullOrBlank()}
        addressTypeEditText.validate(message){s -> !s.isNullOrBlank()}

        personService = PersonServiceFactory.makeService()

        signUpButton.setOnClickListener { signUp() }
    }

    private fun signUp(){
        if(validForm){
            val person = Person(firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                emailAddr = emailEditText.text.toString(),
                birthYear = birthYearEditText.text.toString())

            personService.signUpPerson(person).enqueue(object:
                Callback<Person?> {
                override fun onFailure(call: Call<Person?>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Oops something went wrong please check your internet connection", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<Person?>,
                    response: Response<Person?>
                ) {
                    if (response.body() != null && response.body()?.personId != 0L) { // our business rule is that if status is true then entity is not null
                        Toast.makeText(this@SignUpActivity, "OK", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignUpActivity, "Something went terribly wrong", Toast.LENGTH_LONG).show()
                    }
                }
            })

        }else{
            Toast.makeText(this@SignUpActivity, "Erro no formulário!", Toast.LENGTH_LONG).show()
        }
    }


    // Helper function to validate the form depending on the state of the edit text
    private fun EditText.beforeTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                afterTextChanged.invoke(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    // Validate some Edit text
    // Arguments: an error message, a boolean expression (the validation expression)
    private fun EditText.validate(message: String, validator: (String) -> Boolean) {
        this.beforeTextChanged {
            this.error = if (validator(it)) null else message
            validForm = validator(it)
        }
        this.error = if (validator(this.text.toString())) null else message
    }

}
