package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.databinding.ActivityRegistrationBinding
import ie.setu.fitnessapp.models.SignInModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var model = SignInModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Set up click listener for the login button
        binding.txtLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerUser() {
        model.userName = binding.txtUsername.text.toString()
        model.email = binding.txtEmail.text.toString()
        model.password = binding.txtPassword.text.toString()

        if (model.email.isNotEmpty() && model.password.isNotEmpty() && model.userName.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(model.email, model.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            val user = hashMapOf(
                                "username" to model.userName,
                                "email" to model.email
                            )
                            firebaseFirestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    navigateToLogin()
                                }
                                .addOnFailureListener { exception ->
                                    showErrorSnackbar("Error adding user data to Firestore: $exception")
                                }
                        } else {
                            showErrorSnackbar("User ID is null")
                        }
                    } else {
                        showErrorSnackbar("Registration failed: ${task.exception?.message}")
                    }
                }
        } else {
            showErrorSnackbar("Please enter email, password, and username")
        }
    }


    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
