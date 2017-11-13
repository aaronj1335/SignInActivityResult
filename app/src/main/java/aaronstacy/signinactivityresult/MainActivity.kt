package aaronstacy.signinactivityresult

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {
  private val SIGN_IN_CODE: Int = 9001
  private var calledSignIn: Boolean = false
  private val CALLED_SIGN_IN: String = "calledSignIn"

  private val googleSignInClient: GoogleSignInClient by lazy {
    GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("BLERG", "onCreate " + savedInstanceState?.getBoolean(CALLED_SIGN_IN))
    if (savedInstanceState != null)
      calledSignIn = savedInstanceState.getBoolean(CALLED_SIGN_IN, false)
    setContentView(R.layout.activity_main)
  }

  override fun onStart() {
    super.onStart()
    if (calledSignIn) return
    calledSignIn = true
    googleSignInClient.signOut()
        .continueWith {
          Log.d("BLERG", "Signed out: " + it.result)
          startActivityForResult(googleSignInClient.signInIntent, SIGN_IN_CODE)
        }
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putBoolean(CALLED_SIGN_IN, calledSignIn)
    Log.d("BLERG", "saving instance state " + outState?.getBoolean(CALLED_SIGN_IN))
    super.onSaveInstanceState(outState)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SIGN_IN_CODE) {
      GoogleSignIn.getSignedInAccountFromIntent(data)
          .continueWith {
            if (!it.isSuccessful) {
              val e = it.exception
              if (e is ApiException)
                if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS)
                  Log.e("BLERG", "SIGN_IN_CURRENTLY_IN_PROGRESS")
            }
            it
          }
    }
  }
}
