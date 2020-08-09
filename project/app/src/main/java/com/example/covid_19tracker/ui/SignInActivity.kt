package com.example.covid_19tracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.covid_19tracker.R
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesManager
import com.example.covid_19tracker.model.Person
import com.example.covid_19tracker.service.PersonService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : BasicActivity() {
    private val RC_SIGN_IN = 9001
    private val TAG = "SignInActivity"
    private var mAuth: FirebaseAuth? = null

    private lateinit var personService: PersonService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val account = GoogleSignIn.getLastSignedInAccount(this)

        val signInButton: SignInButton = findViewById<SignInButton>(R.id.sign_in_button)
        if (signInButton != null) run {
            signInButton.setOnClickListener() { view ->
                signIn(view)
            }
        }

        personService = PersonService.create()
    }


    private fun signIn(view: View) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            // Set signed in status
            SharedPreferencesManager.setBoolean(SharedPreferenceKeys.LOGGED_IN_PREF, true)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            // If fist sign in, save the user preferences
            if(SharedPreferencesManager.firstLogin() == true){
                SharedPreferencesManager.setBoolean(SharedPreferenceKeys.FIRST_LOGIN, false)
                if (account != null) {
                    firstLogin(account)
                }
            }else{
                // Signed in successfully, show authenticated UI
                // Show success message
                Toast.makeText(this, "Logado", Toast.LENGTH_SHORT).show()
                // Return to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Erro no login!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     *  Save the user information in the shared preferences
     */
    private fun firstLogin(account: GoogleSignInAccount){
        val googleId = account.id ?: ""
        Log.i("Google ID", googleId)
        SharedPreferencesManager.setString(SharedPreferenceKeys.GOOGLE_ID, googleId)

        val googleFirstName = account.givenName ?: ""
        Log.i("Google First Name", googleFirstName)
        SharedPreferencesManager.setString(SharedPreferenceKeys.GOOGLE_FIRST_NAME, googleFirstName)

        val googleLastName = account.familyName ?: ""
        Log.i("Google Last Name", googleLastName)
        SharedPreferencesManager.setString(SharedPreferenceKeys.GOOGLE_LAST_NAME, googleLastName)

        val googleEmail = account.email ?: ""
        Log.i("Google Email", googleEmail)
        SharedPreferencesManager.setString(SharedPreferenceKeys.GOOGLE_EMAIL, googleEmail)

        val googleProfilePicURL = account.photoUrl.toString()
        Log.i("Google Profile Pic URL", googleProfilePicURL)
        SharedPreferencesManager.setString(
            SharedPreferenceKeys.GOOGLE_PROFILE_PICTURE_URL,
            googleProfilePicURL
        )

        val googleIdToken = account.idToken ?: ""
        Log.i("Google ID Token", googleIdToken)
        SharedPreferencesManager.setString(SharedPreferenceKeys.GOOGLE_ID_TOKEN, googleIdToken)

        val person = Person(
            firstName = googleFirstName,
            lastName = googleLastName,
            emailAddr = googleEmail,
            googleId = googleId,
            googleIdToken = googleIdToken,
            googleProfilePictureUrl = googleProfilePicURL
        )

        val context: Context = this
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

        // Go to sign up activity
        intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}