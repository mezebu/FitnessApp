package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.databinding.ActivityLoginBinding
import timber.log.Timber
import timber.log.Timber.i

class LoginActivity : AppCompatActivity() {

    private lateinit var txtEmail: TextInputEditText
    private lateinit var txtPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegister: TextView
    private lateinit var txtLayUser: TextInputLayout
    private lateinit var txtLayPass: TextInputLayout
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        i("Login Activity started..")

        // Initialize views using view binding
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)
        txtLayUser = findViewById(R.id.txtLay_user)
        txtLayPass = findViewById(R.id.txtLay_pass)

        // Set up click listener for the register button
        txtRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Set up click listener for the login button
        binding.btnLogin.setOnClickListener {
            i("Login Button Pressed")
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isEmpty()) {
                Snackbar
                    .make(it,"Please Enter an Email", Snackbar.LENGTH_LONG)
                    .show()
            }

            if (password.isEmpty()) {
                Snackbar
                    .make(it,"Please Enter Your Password", Snackbar.LENGTH_LONG)
                    .show()
            }

            // Perform login operation here
        }
    }
}
