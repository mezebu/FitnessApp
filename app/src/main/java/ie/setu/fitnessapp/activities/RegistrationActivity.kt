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

        // Inflate layout using view binding
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth and FirebaseFirestore instances
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Set up click listener for the "Already have an account? Login" text view
        binding.txtLogin.setOnClickListener {
            navigateToLogin()
        }

        // Set up click listener for the register button
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    // Function to navigate to the login activity
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    // Function to register a new user
    private fun registerUser() {
        // Get user input from text fields
        model.userName = binding.txtUsername.text.toString()
        model.email = binding.txtEmail.text.toString()
        model.password = binding.txtPassword.text.toString()

        // Check if email, password, and username are not empty
        if (model.email.isNotEmpty() && model.password.isNotEmpty() && model.userName.isNotEmpty()) {
            // Create user with email and password using FirebaseAuth
            firebaseAuth.createUserWithEmailAndPassword(model.email, model.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            // Create user data map to be stored in Firestore
                            val user = hashMapOf(
                                "username" to model.userName,
                                "email" to model.email
                            )
                            // Add user data to Firestore
                            firebaseFirestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    // If user data is added successfully, navigate to login activity
                                    navigateToLogin()
                                }
                                .addOnFailureListener { exception ->
                                    // If user data addition fails, show error message
                                    showErrorSnackbar("Error adding user data to Firestore: $exception")
                                }
                        } else {
                            // If user ID is null, show error message
                            showErrorSnackbar("User ID is null")
                        }
                    } else {
                        // If user creation fails, show error message
                        showErrorSnackbar("Registration failed: ${task.exception?.message}")
                    }
                }
        } else {
            // If any of the required fields are empty, show error message
            showErrorSnackbar("Please enter email, password, and username")
        }
    }

    // Function to show a Snackbar with an error message
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
