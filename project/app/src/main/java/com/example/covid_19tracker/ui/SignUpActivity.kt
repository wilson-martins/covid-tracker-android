package com.example.covid_19tracker.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.service.PersonService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    // Control variables
    var validForm = false

    // Layout components
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var birthYearTextView: TextView
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var birthYearEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var oldTextColor: ColorStateList
    private lateinit var personService: PersonService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize components
        firstNameTextView = findViewById(R.id.first_name)
        firstNameEditText = findViewById(R.id.edit_first_name)
        firstNameEditText.validate("Campo obrigatório!"){firstNameEditText.text.isNullOrBlank()}
        lastNameTextView = findViewById(R.id.last_name)
        lastNameEditText = findViewById(R.id.edit_last_name)
        lastNameEditText.validate("Campo obrigatório!"){lastNameEditText.text.isNullOrBlank()}
        emailTextView = findViewById(R.id.email)
        emailEditText = findViewById(R.id.edit_email)
        emailEditText.validate("Campo obrigatório!"){emailEditText.text.isNullOrBlank()}
        birthYearTextView = findViewById(R.id.birth_year)
        birthYearEditText = findViewById(R.id.edit_birth_year)
        signUpButton = findViewById(R.id.sign_up_button)
        oldTextColor = firstNameTextView.textColors
        personService = PersonService.create()
        signUpButton.setOnClickListener { signUp() }

        val spinner: Spinner = findViewById(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.address_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun signUp() {
        if (validForm) {
            val person = Person(
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                emailAddr = emailEditText.text.toString(),
                birthYear = birthYearEditText.text.toString(),
                googleId = SharedPreferencesSettings.loadString(
                    this,
                    SharedPreferenceKeys.GOOGLE_ID
                ) ?: "",
                googleIdToken = SharedPreferencesSettings.loadString(
                    this,
                    SharedPreferenceKeys.GOOGLE_ID_TOKEN
                ) ?: "",
                googleProfilePictureUrl = SharedPreferencesSettings.loadString(
                    this,
                    SharedPreferenceKeys.GOOGLE_PROFILE_PICTURE_URL
                ) ?: ""
            )

            personService.signUpPerson(person).enqueue(object :
                Callback<Person?> {
                override fun onFailure(call: Call<Person?>, t: Throwable) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Oops something went wrong please check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<Person?>,
                    response: Response<Person?>
                ) {
                    if (response.body() != null && response.body()?.personId != 0L) {
                        SharedPreferencesSettings.setLong(
                            this@SignUpActivity,
                            SharedPreferenceKeys.PERSON_ID,
                            response.body()?.personId ?: 0
                        )
                        Toast.makeText(this@SignUpActivity, "OK", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Something went terribly wrong",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        } else {
            Toast.makeText(this@SignUpActivity, "Erro no formulário!", Toast.LENGTH_LONG).show()
        }
    }

    // Helper function to validate the form depending on the state of the edit text
    private fun EditText.beforeTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                afterTextChanged.invoke(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
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
