package ie.setu.fitnessapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.databinding.ActivityAddFitnessBinding
import ie.setu.fitnessapp.models.FitnessDataModel
import timber.log.Timber
import timber.log.Timber.i

class AddFitnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFitnessBinding
    private var model = FitnessDataModel()
    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityAddFitnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
        i("Add Activity started..")

        binding.addActivity.setOnClickListener {
            addActivity()
            i("add fitness activities button pressed: ${model.exerciseName}")
        }
    }

    private fun addActivity() {
        model.exerciseName = binding.editTextExerciseName.text.toString()
        model.duration = binding.editTextDuration.text.toString()
        model.weight = binding.editTextWeight.text.toString()
        model.calories = binding.editTextCalories.text.toString()
        model.endDate = binding.editTextEndDate.text.toString()
        model.note = binding.editTextNote.text.toString()

        if (model.endDate.isEmpty()) {
            showSnackbar("Please enter an end date")
            return
        }

        val dateFormatRegex = Regex("\\d{4}-\\d{2}-\\d{2}")
        if (!dateFormatRegex.matches(model.endDate)) {
            showSnackbar("Invalid end date format. Please use YYYY-MM-DD")
            return
        }

        val fitnessData = hashMapOf(
            "exerciseName" to model.exerciseName,
            "duration" to model.duration,
            "weight" to model.weight,
            "calories" to model.calories,
            "endDate" to model.endDate,
            "note" to model.note
        )

        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
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