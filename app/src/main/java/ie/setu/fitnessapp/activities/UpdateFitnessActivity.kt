package ie.setu.fitnessapp.activities

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.databinding.ActivityUpdateFitnessBinding
import ie.setu.fitnessapp.models.FitnessDataModel
import timber.log.Timber

class UpdateFitnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateFitnessBinding
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using view binding
        binding = ActivityUpdateFitnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get document ID from intent
        documentId = intent.getStringExtra("documentId") ?: ""

        // Check if document ID is empty
        if (TextUtils.isEmpty(documentId)) {
            // If empty, show error message and finish activity
            showSnackbar("Error: No fitness record specified")
            finish()
            return
        }

        // Load data corresponding to the document ID
        loadData()

        // Set up click listener for the update button
        binding.btnUpdateActivity.setOnClickListener {
            updateActivity()
        }
    }

    // Function to load data from Firestore
    private fun loadData() {
        currentUser?.let { user ->
            db.collection("fitness_data").document(user.uid)
                .collection("data").document(documentId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // If document exists, convert it to FitnessDataModel and display data
                        val fitnessData = document.toObject(FitnessDataModel::class.java)
                        fitnessData?.let {
                            displayData(it)
                        }
                    } else {
                        // If document does not exist, log error and finish activity
                        Timber.e("No such document")
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    // If loading data fails, log error and show error message
                    Timber.e("Error loading document")
                    showSnackbar("Failed to load data: ${e.message}")
                }
        } ?: run {
            // If current user is null, show error message and finish activity
            showSnackbar("User not authenticated")
            finish()
        }
    }

    // Function to display data on the UI
    private fun displayData(fitnessData: FitnessDataModel) {
        binding.editTextExerciseName.setText(fitnessData.exerciseName)
        binding.editTextDuration.setText(fitnessData.duration)
        binding.editTextWeight.setText(fitnessData.weight)
        binding.editTextCalories.setText(fitnessData.calories)
        binding.editTextEndDate.setText(fitnessData.endDate)
        binding.editTextNote.setText(fitnessData.note)
    }

    // Function to update fitness activity data
    private fun updateActivity() {
        // Create map of updated fitness data
        val fitnessDataUpdates = mapOf(
            "exerciseName" to binding.editTextExerciseName.text.toString().trim(),
            "duration" to binding.editTextDuration.text.toString().trim(),
            "weight" to binding.editTextWeight.text.toString().trim(),
            "calories" to binding.editTextCalories.text.toString().trim(),
            "endDate" to binding.editTextEndDate.text.toString().trim(),
            "note" to binding.editTextNote.text.toString().trim()
        )

        // Check if any field is blank
        if (fitnessDataUpdates.values.any { it.isBlank() }) {
            // If any field is blank, show error message and return
            showSnackbar("Please fill in all fields")
            return
        }

        // Update data in Firestore
        currentUser?.let { user ->
            db.collection("fitness_data").document(user.uid)
                .collection("data").document(documentId).update(fitnessDataUpdates)
                .addOnSuccessListener {
                    // If update is successful, log success message, show Snackbar, and finish activity
                    Timber.i("DocumentSnapshot successfully updated")
                    showSnackbar("Fitness data updated successfully!")
                    finish()
                }
                .addOnFailureListener { e ->
                    // If update fails, log error and show error message
                    Timber.e("Error updating document")
                    showSnackbar("Update failed: ${e.message}")
                }
        } ?: showSnackbar("User not authenticated")
    }

    // Function to show a Snackbar with a message
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
