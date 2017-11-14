package aaronstacy.signinactivityresult

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.GoogleApiClient

class MainActivity : AppCompatActivity() {
  private val SIGN_IN_CODE: Int = 9001
  private var calledSignIn: Boolean = false
  private val CALLED_SIGN_IN: String = "calledSignIn"

  private val googleApiClient: GoogleApiClient by lazy {
    GoogleApiClient.Builder(this,
        object: GoogleApiClient.ConnectionCallbacks {
          override fun onConnected(p0: Bundle?) {
            this@MainActivity.onConnected()
          }

          override fun onConnectionSuspended(p0: Int) {
            TODO("onConnectionSuspended not implemented")
          }
        },
        GoogleApiClient.OnConnectionFailedListener {
          TODO("onConnectionFailed not implemented")
        })
        .addApi(Auth.GOOGLE_SIGN_IN_API, GoogleSignInOptions.DEFAULT_SIGN_IN)
        .build()
  }

  private fun onConnected() {
    if (calledSignIn) return
    calledSignIn = true
    Log.d("BLERG", "calling signOut + startActivityForResult")
    Auth.GoogleSignInApi.signOut(googleApiClient)
        .setResultCallback {
          if (!it.isSuccess) {
            Log.d("BLERG", "failed to sign out")
            return@setResultCallback
          }

          Log.d("BLERG", "signed out, signing in")
          startActivityForResult(
              Auth.GoogleSignInApi.getSignInIntent(googleApiClient),
              SIGN_IN_CODE)
        }
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
    googleApiClient.connect()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putBoolean(CALLED_SIGN_IN, calledSignIn)
    Log.d("BLERG", "saving instance state " + outState?.getBoolean(CALLED_SIGN_IN))
    super.onSaveInstanceState(outState)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SIGN_IN_CODE) {
      Log.d("BLERG", "Got result for SIGN_IN_CODE")
      var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
      if (result.isSuccess) {
        Log.d("BLERG", "Got sign in result: " + result.signInAccount)
      } else {
        if (result.status.statusCode == GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS)
          Log.e("BLERG", "SIGN_IN_CURRENTLY_IN_PROGRESS")
      }
    }
  }
}
