package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ie.setu.fitnessapp.R

class LoginActivity : AppCompatActivity() {

    private lateinit var txtEmail: TextInputEditText
    private lateinit var txtPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegister: TextView
    private lateinit var txtLayUser: TextInputLayout
    private lateinit var txtLayPass: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isEmpty()) {
                txtLayUser.error = getString(R.string.error_empty_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                txtLayPass.error = getString(R.string.error_empty_password)
                return@setOnClickListener
            }

            // Perform login operation here
        }
    }
}
