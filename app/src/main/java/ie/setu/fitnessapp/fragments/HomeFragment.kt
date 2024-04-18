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
import ie.setu.fitnessapp.adapters.FitnessAdapter
import ie.setu.fitnessapp.models.FitnessDataModel

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fitnessAdapter: FitnessAdapter
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupRecyclerView(view)
        setupFabButton(view)
        fetchDataFromFirestore()
        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.fitnessRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupFabButton(view: View) {
        val fabButton: FloatingActionButton = view.findViewById(R.id.fab)
        fabButton.setOnClickListener { navigateToAddFitness() }
    }

    private fun fetchDataFromFirestore() {
        firebaseAuth.currentUser?.let { user ->
            db.collection("fitness_data").document(user.uid).collection("data")
                .get()
                .addOnSuccessListener { documents ->
                    val fitnessList = documents.mapNotNull { document ->
                        document.toFitnessDataModel()
                    }
                    updateRecyclerView(fitnessList)
                }
                .addOnFailureListener { exception ->
                    showSnackbar(exception.message ?: "Error loading fitness data")
                }
        } ?: showSnackbar("User not logged in.")
    }

    private fun DocumentSnapshot.toFitnessDataModel(): FitnessDataModel {
        return FitnessDataModel(
            exerciseName = getString("exerciseName") ?: "",
            duration = getString("duration") ?: "",
            weight = getString("weight") ?: "",
            calories = getString("calories") ?: "",
            endDate = getString("endDate") ?: "",
            note = getString("note") ?: ""
        )
    }

    private fun updateRecyclerView(fitnessList: List<FitnessDataModel>) {
        fitnessAdapter = FitnessAdapter(fitnessList)
        recyclerView.adapter = fitnessAdapter
    }

    private fun showSnackbar(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    private fun navigateToAddFitness() {
        startActivity(Intent(activity, AddFitnessActivity::class.java))
    }
}
