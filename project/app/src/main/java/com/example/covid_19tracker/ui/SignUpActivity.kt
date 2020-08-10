package com.example.covid_19tracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.service.PersonService
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class SignUpActivity : AppCompatActivity(){
    // Control variables
    protected var validForm = true

    // Layout components
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var birthYearEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var zipCodeEditText: EditText
    private lateinit var addressTypeSpinner: Spinner
    private lateinit var signUpButton: Button
    private lateinit var personService: PersonService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initializeUIComponents()
        fillFieldsWithUserData()
    }

    private fun initializeUIComponents() {
        // Mandatory fields
        firstNameEditText = findViewById(R.id.edit_first_name)
        firstNameEditText.validate("Campo obrigatório!"){!firstNameEditText.text.isNullOrBlank()}
        lastNameEditText = findViewById(R.id.edit_last_name)
        lastNameEditText.validate("Campo obrigatório!"){!lastNameEditText.text.isNullOrBlank()}
        emailEditText = findViewById(R.id.edit_email)
        emailEditText.validate("Campo obrigatório!"){!emailEditText.text.isNullOrBlank()}

        birthYearEditText = findViewById(R.id.edit_birth_year)
        countryEditText = findViewById(R.id.edit_contry)
        stateEditText = findViewById(R.id.edit_state)
        cityEditText = findViewById(R.id.edit_city)
        zipCodeEditText = findViewById(R.id.edit_zip_code)
        addressTypeSpinner = findViewById(R.id.spinner_type_address)
        signUpButton = findViewById(R.id.sign_up_button)

        personService = PersonService.create()

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.address_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            addressTypeSpinner.adapter = adapter
        }
        signUpButton.setOnClickListener{signUp()}
    }

    private fun fillFieldsWithUserData() {
        firstNameEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.GOOGLE_FIRST_NAME)
        )
        lastNameEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.GOOGLE_LAST_NAME)
        )
        emailEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.GOOGLE_EMAIL)
        )
        birthYearEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.BIRTH_YEAR)
        )
        countryEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.COUNTRY)
        )
        stateEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.STATE)
        )
        cityEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.CITY)
        )
        zipCodeEditText.setText(
            SharedPreferencesManager.loadString(SharedPreferenceKeys.ZIP_CODE)
        )
        val pos =  SharedPreferencesManager.loadLong(SharedPreferenceKeys.ADDRESS_TYPE)
        if (pos != null)
            addressTypeSpinner.setSelection(pos.toInt())

    }
    private fun getContext() : Context {
        return this@SignUpActivity
    }

    protected open fun signUp() {
        val context = getContext()
        if (validForm) {
            val person = Person(
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                emailAddr = emailEditText.text.toString(),
                birthYear = birthYearEditText.text.toString(),
                googleId = SharedPreferencesManager.loadString(
                    SharedPreferenceKeys.GOOGLE_ID
                ) ?: "",
                googleIdToken = SharedPreferencesManager.loadString(
                    SharedPreferenceKeys.GOOGLE_ID_TOKEN
                ) ?: "",
                googleProfilePictureUrl = SharedPreferencesManager.loadString(
                    SharedPreferenceKeys.GOOGLE_PROFILE_PICTURE_URL
                ) ?: ""
            )
            personService.signUpPerson(person).enqueue(object :
                Callback<Person?> {
                override fun onFailure(call: Call<Person?>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Oops something went wrong please check your internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<Person?>,
                    response: Response<Person?>
                ) {
                    if (response.body() != null && response.body()?.personId != 0L) {
                        SharedPreferencesManager.setLong(
                            SharedPreferenceKeys.PERSON_ID,
                            response.body()?.personId ?: 0
                        )
                        Toast.makeText(context, "OK", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Something went terribly wrong",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
            saveUserDataOnSharedPreferences()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(context, "Erro no formulário!", Toast.LENGTH_LONG).show()
        }
    }

    protected fun saveUserDataOnSharedPreferences() {
        SharedPreferencesManager.setString(
            SharedPreferenceKeys.BIRTH_YEAR, edit_birth_year.text.toString()
        )
        SharedPreferencesManager.setString(SharedPreferenceKeys.COUNTRY, edit_contry.text.toString())
        SharedPreferencesManager.setString(SharedPreferenceKeys.STATE, edit_state.text.toString())
        SharedPreferencesManager.setString(SharedPreferenceKeys.CITY, edit_city.text.toString())
        SharedPreferencesManager.setString(SharedPreferenceKeys.ZIP_CODE, edit_zip_code.text.toString())
        SharedPreferencesManager.setLong(
            SharedPreferenceKeys.ADDRESS_TYPE,
            addressTypeSpinner.selectedItemPosition.toLong()
        )
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

