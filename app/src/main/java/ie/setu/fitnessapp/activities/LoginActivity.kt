package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.databinding.ActivityLoginBinding
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install splash screen
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
        Timber.i("Login Activity started..")

        // Initialize views using view binding
        val btnLogin = binding.btnLogin
        val txtRegister = binding.txtRegister

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Set up click listener for the register button
        txtRegister.setOnClickListener {
            navigateToRegistration()
        }

        // Set up click listener for the login button
        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun loginUser() {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navigateToMain()
                    } else {
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

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
