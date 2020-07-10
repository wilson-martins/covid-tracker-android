package com.example.covid_19tracker.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19tracker.R
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.service.PersonService
import com.example.covid_19tracker.service.PersonServiceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(){

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
        lastNameTextView = findViewById(R.id.last_name)
        lastNameEditText = findViewById(R.id.edit_last_name)
        emailTextView = findViewById(R.id.email)
        emailEditText = findViewById(R.id.edit_email)
        birthYearTextView = findViewById(R.id.birth_year)
        birthYearEditText = findViewById(R.id.edit_birth_year)
        signUpButton = findViewById(R.id.sign_up_button)
        signUpButton.setOnClickListener { view->
            signUp()
        }
        oldTextColor = firstNameTextView.textColors

        personService = PersonServiceFactory.makeService()
    }

    private fun signUp(){
        var validForm: Boolean = validateSignUpForm()
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
        }
    }

    private fun validateSignUpForm(): Boolean{
        // TODO: fix this validation form
        val editTextArray = arrayOf(firstNameEditText, lastNameEditText, emailEditText, birthYearEditText)
        val textViewArray = arrayOf(firstNameTextView, lastNameTextView, emailTextView, birthYearTextView)
        var okFlag: Boolean = true
        val oldTextLabels: ArrayList<String> = ArrayList()
        for(textView in textViewArray){
            oldTextLabels.add(textView.text.toString())
        }
        var i = 0
        for( (editText, textView) in editTextArray.zip(textViewArray)){
            if(editText.text.isEmpty()){
                textView.setTextColor(getColor(R.color.colorRed))
                val currText: String = textView.text.toString()
                textView.text = currText + " " +  resources.getString(R.string.signUpErrorMessage)
                okFlag = false
            }else{
                textView.setTextColor(oldTextColor)
                textView.text = oldTextLabels[i]
            }
            i += 1
        }
        return okFlag
    }
}
