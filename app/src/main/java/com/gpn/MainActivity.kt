package com.gpn

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gpn.navigation.NavGraph
import com.gpn.network.MyFirebaseHelper
import com.gpn.ui.LoginScreen
import com.gpn.viewmodel.GasPriceViewModel
import com.gpn.viewmodel.PriceAlertsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val gasPriceViewModel: GasPriceViewModel = hiltViewModel()
                val gasAlertsViewModel: PriceAlertsViewModel = hiltViewModel()

                // Show Login Screen with Google Sign-In Button
                LoginScreen(onGoogleSignIn = { signInWithGoogle() })

                NavGraph(
                    gasPriceViewModel = gasPriceViewModel,
                    alertsModel = gasAlertsViewModel
                )
            }
        }

        setupGoogleSignIn() // ✅ Initialize Google Sign-In
    }

    // 🔹 Step 1: Setup Google Sign-In
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure correct client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    // 🔹 Step 2: Launch Google Sign-In
    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // 🔹 Step 3: Handle Sign-In Result
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("Auth", "✅ Google Sign-In successful, ID Token: ${account.idToken}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                Log.e("Auth", "❌ Google Sign-In failed", e)
            }
        }

    // 🔹 Step 4: Authenticate with Firebase
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "✅ Firebase Authentication Successful: ${firebaseAuth.currentUser?.uid}")
                    MyFirebaseHelper.fetchFCMToken() // ✅ Fetch FCM Token after login
                    restartApp() // ✅ Refresh UI after login
                } else {
                    Log.e("Auth", "❌ Firebase Authentication Failed", task.exception)
                }
            }
    }

    // 🔹 Restart the Activity to Reload UI After Login
    private fun restartApp() {
        finish()
        startActivity(intent)
    }


}
