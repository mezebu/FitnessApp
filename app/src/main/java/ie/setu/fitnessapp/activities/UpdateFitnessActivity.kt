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
        binding = ActivityUpdateFitnessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        documentId = intent.getStringExtra("documentId") ?: ""

        if (TextUtils.isEmpty(documentId)) {
            showSnackbar("Error: No fitness record specified")
            finish()
            return
        }

        loadData()

        binding.btnUpdateActivity.setOnClickListener {
            updateActivity()
        }
    }

    private fun loadData() {
        currentUser?.let { user ->
            db.collection("fitness_data").document(user.uid)
                .collection("data").document(documentId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fitnessData = document.toObject(FitnessDataModel::class.java)
                        fitnessData?.let {
                            displayData(it)
                        }
                    } else {
                        Timber.e("No such document")
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Timber.e("Error loading document")
                    showSnackbar("Failed to load data: ${e.message}")
                }
        } ?: run {
            showSnackbar("User not authenticated")
            finish()
        }
    }

    private fun displayData(fitnessData: FitnessDataModel) {
        binding.editTextExerciseName.setText(fitnessData.exerciseName)
        binding.editTextDuration.setText(fitnessData.duration)
        binding.editTextWeight.setText(fitnessData.weight)
        binding.editTextCalories.setText(fitnessData.calories)
        binding.editTextEndDate.setText(fitnessData.endDate)
        binding.editTextNote.setText(fitnessData.note)
    }

    private fun updateActivity() {
        val fitnessDataUpdates = mapOf(
            "exerciseName" to binding.editTextExerciseName.text.toString().trim(),
            "duration" to binding.editTextDuration.text.toString().trim(),
            "weight" to binding.editTextWeight.text.toString().trim(),
            "calories" to binding.editTextCalories.text.toString().trim(),
            "endDate" to binding.editTextEndDate.text.toString().trim(),
            "note" to binding.editTextNote.text.toString().trim()
        )

        if (fitnessDataUpdates.values.any { it.isBlank() }) {
            showSnackbar("Please fill in all fields")
            return
        }

        currentUser?.let { user ->
            db.collection("fitness_data").document(user.uid)
                .collection("data").document(documentId).update(fitnessDataUpdates)
                .addOnSuccessListener {
                    Timber.i("DocumentSnapshot successfully updated")
                    showSnackbar("Fitness data updated successfully!")
                    finish()
                }
                .addOnFailureListener { e ->
                    Timber.e("Error updating document")
                    showSnackbar("Update failed: ${e.message}")
                }
        } ?: showSnackbar("User not authenticated")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
