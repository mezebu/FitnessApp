package ie.setu.fitnessapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.databinding.ActivityAddUserDetailsBinding

class AddUserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityAddUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveDetails.setOnClickListener {
            saveUserDetails()
        }
    }

    private fun saveUserDetails() {
        val age = binding.editTextUserAge.text.toString().trim()
        val address = binding.editTextUserAddress.text.toString().trim()
        val profession = binding.editTextUserProfession.text.toString().trim()

        if (age.isEmpty() || address.isEmpty() || profession.isEmpty()) {
            showSnackbar("Please fill in all the fields")
            return
        }

        val userDetails = hashMapOf(
            "age" to age,
            "address" to address,
            "profession" to profession,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firebaseAuth.currentUser?.let { user ->
            db.collection("user_details").document(user.uid).set(userDetails)
                .addOnSuccessListener {
                    showSnackbar("User details saved successfully!")
                    clearEditTextFields()
                    navigateToFitnessActivity()
                }
                .addOnFailureListener { exception ->
                    showSnackbar("Error saving user details: ${exception.message}")
                }
        } ?: run {
            showSnackbar("User not logged in.")
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun clearEditTextFields() {
        binding.editTextUserAge.text?.clear()
        binding.editTextUserAddress.text?.clear()
        binding.editTextUserProfession.text?.clear()
    }

    private fun navigateToFitnessActivity() {
        val intent = Intent(this, FitnessActivity::class.java)
        startActivity(intent)
    }
}
