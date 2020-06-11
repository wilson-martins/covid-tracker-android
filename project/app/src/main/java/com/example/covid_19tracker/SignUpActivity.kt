package com.example.covid_19tracker

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
    }

    private fun signUp(){
        var validForm: Boolean = validateSignUpForm()
        if(validForm){
            //TODO: create the user in the data base
        }
    }

    private fun validateSignUpForm(): Boolean{
        // TODO: fix this validation form
        val editTextArray = arrayOf(firstNameEditText, lastNameEditText, emailEditText, birthYearEditText)
        val textViewArray = arrayOf(firstNameTextView, lastNameTextView, emailTextView, birthYearTextView)
        var okFlag: Boolean = true
        var oldTextLabels: ArrayList<String> = ArrayList()
        for(textView in textViewArray){
            oldTextLabels.add(textView.text.toString())
        }
        var i = 1
        for( (editText, textView) in editTextArray.zip(textViewArray)){
            if(editText.text.isEmpty()){
                textView.setTextColor(getColor(R.color.colorRed))
                var currText:String = textView.text.toString()
                textView.setText(currText + " " +  resources.getString(R.string.signUpErrorMessage))
                okFlag = false
            }else{
                textView.setTextColor(oldTextColor)
                textView.setText(oldTextLabels[i])
            }
            i += 1
        }
        return okFlag
    }
}
