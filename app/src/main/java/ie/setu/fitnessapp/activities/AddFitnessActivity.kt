package ie.setu.fitnessapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.databinding.ActivityAddFitnessBinding
import ie.setu.fitnessapp.models.FitnessDataModel
import timber.log.Timber

class AddFitnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFitnessBinding
    private var model = FitnessDataModel()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityAddFitnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
        Timber.i("Add Activity started..")

        binding.addActivity.setOnClickListener {
            addActivity()
            Timber.i("add fitness activities button pressed: ${model.exerciseName}")
        }
    }

    private fun addActivity() {
        val exerciseName = binding.editTextExerciseName.text.toString().trim()
        val duration = binding.editTextDuration.text.toString().trim()
        val weight = binding.editTextWeight.text.toString().trim()
        val calories = binding.editTextCalories.text.toString().trim()
        val endDate = binding.editTextEndDate.text.toString().trim()
        val note = binding.editTextNote.text.toString().trim()

        // Check for empty fields and specific conditions
        if (exerciseName.isEmpty()) {
            showSnackbar("Exercise name cannot be empty")
            return
        }

        if (duration.isEmpty()) {
            showSnackbar("Duration cannot be empty")
            return
        }

        if (weight.isEmpty()) {
            showSnackbar("Weight cannot be empty")
            return
        }

        if (calories.isEmpty()) {
            showSnackbar("Calories cannot be empty")
            return
        }

        if (endDate.isEmpty()) {
            showSnackbar("Please enter an end date")
            return
        }

        if (note.isEmpty()) {
            showSnackbar("Description cannot be empty")
            return
        }

        val dateFormatRegex = Regex("\\d{2}-\\d{2}-\\d{4}")
        if (!dateFormatRegex.matches(endDate)) {
            showSnackbar("Invalid end date format. Please use DD-MM-YYYY")
            return
        }

        // If all checks pass, update the model and proceed with Firestore upload
        model.apply {
            this.exerciseName = exerciseName
            this.duration = duration
            this.weight = weight
            this.calories = calories
            this.endDate = endDate
            this.note = note
        }

        val fitnessData = hashMapOf(
            "exerciseName" to model.exerciseName,
            "duration" to model.duration,
            "weight" to model.weight,
            "calories" to model.calories,
            "endDate" to model.endDate,
            "note" to model.note,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firebaseAuth.currentUser?.let { user ->
            val userId = user.uid
            db.collection("fitness_data").document(userId).collection("data").document()
                .set(fitnessData)
                .addOnSuccessListener {
                    showSnackbar("Fitness data saved successfully!")
                    clearEditTextFields()
                    navigateToFitnessActivity()
                }
                .addOnFailureListener { exception ->
                    showSnackbar("Error saving fitness data: ${exception.message}")
                }
        } ?: showSnackbar("User not logged in.")
    }


    private fun navigateToFitnessActivity() {
        val intent = Intent(this, FitnessActivity::class.java)
        startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun clearEditTextFields() {
        binding.editTextExerciseName.text?.clear()
        binding.editTextDuration.text?.clear()
        binding.editTextWeight.text?.clear()
        binding.editTextCalories.text?.clear()
        binding.editTextEndDate.text?.clear()
        binding.editTextNote.text?.clear()
    }

}
