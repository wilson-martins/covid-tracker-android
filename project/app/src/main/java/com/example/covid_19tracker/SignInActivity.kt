package com.example.covid_19tracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.covid_19tracker.common.SharedPreferenceKeys
import com.example.covid_19tracker.common.SharedPreferencesSettings
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BasicActivity() {
    private val RC_SIGN_IN = 9001
    private val TAG = "SignInActivity"
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)

        val signInButton: SignInButton = findViewById<SignInButton>(R.id.sign_in_button)
        if (signInButton != null) run {
            signInButton.setOnClickListener() { view ->
                signIn(view)
            }
        }
    }


    private fun signIn(view: View) {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            // Set signed in status
            SharedPreferencesSettings.setBoolean(this, SharedPreferenceKeys.LOGGED_IN_PREF, true)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // If fist sign in, save the user preferences
            if(SharedPreferencesSettings.firstLogin(this)== true){
                SharedPreferencesSettings.setBoolean(this, SharedPreferenceKeys.FIRST_LOGIN, false)
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
        SharedPreferencesSettings.setString(this, SharedPreferenceKeys.GOOGLE_ID, googleId)

        val googleFirstName = account.givenName ?: ""
        Log.i("Google First Name", googleFirstName)
        SharedPreferencesSettings.setString(this, SharedPreferenceKeys.GOOGLE_FIRST_NAME,
            googleFirstName)

        val googleLastName = account.familyName ?: ""
        Log.i("Google Last Name", googleLastName)
        SharedPreferencesSettings.setString(this, SharedPreferenceKeys.GOOGLE_LAST_NAME,
            googleLastName)

        val googleEmail = account.email ?: ""
        Log.i("Google Email", googleEmail)
        SharedPreferencesSettings.setString(this, SharedPreferenceKeys.GOOGLE_EMAIL,
            googleEmail)

        val googleProfilePicURL = account.photoUrl.toString()
        Log.i("Google Profile Pic URL", googleProfilePicURL)
        SharedPreferencesSettings.setString(this,
            SharedPreferenceKeys.GOOGLE_PROFILE_PICTURE_URL, googleProfilePicURL)

        val googleIdToken = account.idToken ?: ""
        Log.i("Google ID Token", googleIdToken)
        SharedPreferencesSettings.setString(this, SharedPreferenceKeys.GOOGLE_ID_TOKEN,
            googleIdToken)

        // Go to sign up activity
        intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}