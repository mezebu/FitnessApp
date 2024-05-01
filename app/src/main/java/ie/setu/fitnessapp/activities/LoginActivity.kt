package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.databinding.ActivityLoginBinding
import ie.setu.fitnessapp.models.SignInModel
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var model = SignInModel()
    private var googleSignInClient = MutableLiveData<GoogleSignInClient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        Timber.i("Login Activity started..")

        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
        checkIfUserIsLoggedIn()

        binding.btnLogin.setOnClickListener {
            loginUser()
            Timber.i("Login Button Pressed: ${model.email}")
        }

        // Set up click listener for the register button
        binding.txtRegister.setOnClickListener {
            navigateToRegistration()
        }

        setupGoogleSignInCallback()
        binding.googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        binding.googleSignInButton.setColorScheme(SignInButton.COLOR_AUTO)
        binding.googleSignInButton.setOnClickListener { googleSignIn() }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient.value = GoogleSignIn.getClient(this, gso)
    }

    private fun setupGoogleSignInCallback() {
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Timber.i("Google sign in successful")
                    authWithGoogle(account)
                } catch (e: ApiException) {
                    Timber.i("Google sign in failed $e")
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.value!!.signInIntent
        startForResult.launch(signInIntent)
    }

    private fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Timber.i("signInWithCredential:success")
                    navigateToMain()
                } else {
                    Timber.i("signInWithCredential:failure ${task.exception}")
                    showErrorSnackbar("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun loginUser() {
        model.email = binding.txtEmail.text.toString()
        model.password = binding.txtPassword.text.toString()

        if (model.email.isNotEmpty() && model.password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(model.email, model.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.i("Email login successful")
                        navigateToMain()
                    } else {
                        Timber.i("Email login failed: ${task.exception}")
                        showErrorSnackbar("Login failed: ${task.exception?.message}")
                    }
                }
        } else {
            showErrorSnackbar("Please enter email and password")
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, FitnessActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            navigateToMain()
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
