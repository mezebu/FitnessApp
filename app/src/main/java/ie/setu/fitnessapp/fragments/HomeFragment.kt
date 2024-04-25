package ie.setu.fitnessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.activities.AddFitnessActivity
import ie.setu.fitnessapp.activities.UpdateFitnessActivity
import ie.setu.fitnessapp.adapters.FitnessAdapter
import ie.setu.fitnessapp.models.FitnessDataModel
import ie.setu.fitnessapp.adapters.OnFitnessItemClickListener

class HomeFragment : Fragment(), OnFitnessItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fitnessAdapter: FitnessAdapter
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onResume() {
        super.onResume()
        fetchDataFromFirestore() // Fetching data every time fragment resumes
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Set up RecyclerView
        setupRecyclerView(view)
        // Set up Floating Action Button
        setupFabButton(view)
        // Fetch fitness data from Firestore
        fetchDataFromFirestore()
        return view
    }

    // Set up RecyclerView
    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.fitnessRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    // Set up Floating Action Button
    private fun setupFabButton(view: View) {
        val fabButton: FloatingActionButton = view.findViewById(R.id.fab)
        fabButton.setOnClickListener { navigateToAddFitness() }
    }

    // Fetch fitness data from Firestore
    private fun fetchDataFromFirestore() {
        firebaseAuth.currentUser?.let { user ->
            // Retrieve fitness data from Firestore based on user's UID
            db.collection("fitness_data").document(user.uid).collection("data")
                .get()
                .addOnSuccessListener { documents ->
                    // Map Firestore documents to FitnessDataModel objects
                    val fitnessList = documents.mapNotNull { document ->
                        document.toFitnessDataModel().also {
                            it?.documentId = document.id // Ensure documentId is set correctly
                        }
                    }
                    // Update RecyclerView with fetched data
                    updateRecyclerView(fitnessList)
                }
                .addOnFailureListener { exception ->
                    // Show error message if fetching data fails
                    showSnackbar(exception.message ?: "Error loading fitness data")
                }
        } ?: showSnackbar("User not logged in.")
    }

    // Convert DocumentSnapshot to FitnessDataModel
    private fun DocumentSnapshot.toFitnessDataModel(): FitnessDataModel? {
        val exerciseName = getString("exerciseName") ?: ""
        val duration = getString("duration") ?: ""
        val weight = getString("weight") ?: ""
        val calories = getString("calories") ?: ""
        val endDate = getString("endDate") ?: ""
        val note = getString("note") ?: ""
        val timestamp = getTimestamp("timestamp")

        return if (timestamp != null) {
            FitnessDataModel(
                exerciseName = exerciseName,
                duration = duration,
                weight = weight,
                calories = calories,
                endDate = endDate,
                note = note,
                timestamp = timestamp
            )
        } else {
            null
        }
    }

    // Update RecyclerView with new fitness data
    private fun updateRecyclerView(fitnessList: List<FitnessDataModel>) {
        if (fitnessList.isEmpty()) {
            // Show message if there are no fitness activities
            showSnackbar("No fitness activities have been added yet, start adding your activities for tracking")
        } else {
            // Initialize and set up RecyclerView adapter with fetched data
            fitnessAdapter = FitnessAdapter(fitnessList, requireContext(), this)
            recyclerView.adapter = fitnessAdapter
        }
    }

    // Show Snackbar with provided message
    private fun showSnackbar(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    // Navigate to AddFitnessActivity
    private fun navigateToAddFitness() {
        startActivity(Intent(activity, AddFitnessActivity::class.java))
    }

    // Handle item click in RecyclerView
    override fun onItemClick(fitnessData: FitnessDataModel, documentId: String) {
        // Example action: Navigate to an UpdateFitnessActivity with the selected item's details
        val intent = Intent(activity, UpdateFitnessActivity::class.java).apply {
            putExtra("documentId", documentId)
        }
        startActivity(intent)
    }

    // Handle delete button click in RecyclerView item
    override fun onDeleteClick(fitnessData: FitnessDataModel, documentId: String) {
        firebaseAuth.currentUser?.uid?.let { userId ->
            // Delete fitness data document from Firestore
            db.collection("fitness_data").document(userId).collection("data").document(documentId)
                .delete()
                .addOnSuccessListener {
                    fetchDataFromFirestore()  // Refresh data
                    // Show success message
                    showSnackbar("Activity deleted successfully")
                }
                .addOnFailureListener { e ->
                    // Show error message if deletion fails
                    showSnackbar("Error deleting activity: ${e.message}")
                }
        }
    }
}
