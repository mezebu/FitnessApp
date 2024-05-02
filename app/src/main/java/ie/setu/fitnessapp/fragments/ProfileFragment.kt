package ie.setu.fitnessapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ie.setu.fitnessapp.activities.AddUserDetailsActivity
import ie.setu.fitnessapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFabButton()
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            binding.textViewUsername.text = currentUser.displayName ?: "No Display Name"
            binding.textViewEmail.text = currentUser.email ?: "No Email"

            // Fetch user details from Firestore
            firestore.collection("user_details").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.textViewUserAge.text = document.getString("age") ?: "No Age"
                        binding.textViewUserAddress.text = document.getString("address") ?: "No Address"
                        binding.textViewUserProfession.text = document.getString("profession") ?: "No Profession"

                        // Load the image with Glide
                        val imageUrl = document.getString("imageUrl")
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this@ProfileFragment)
                                .load(imageUrl)
                                .into(binding.imageViewUserImage)
                        } else {
                            showErrorSnackbar("No image URL found.")
                        }
                    } else {
                        showErrorSnackbar("User details not found.")
                    }
                }
                .addOnFailureListener {
                    showErrorSnackbar("Error fetching user details: ${it.message}")
                }
        } else {
            showErrorSnackbar("No user logged in.")
        }

        if (currentUser != null) {
            binding.textViewUsername.text = currentUser.displayName
            binding.textViewEmail.text = currentUser.email
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.textViewUsername.text = document.getString("username")
                        binding.textViewEmail.text = currentUser.email // Email directly from Auth
                    } else {
                        // Handle no document found
                        showErrorSnackbar("User details not found")
                    }
                }
                .addOnFailureListener {
                    // Handle errors
                    showErrorSnackbar("Error fetching user details")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupFabButton() {
        binding.btnSaveDetails.setOnClickListener { navigateToAddUserDetailsActivity() }
    }

    private fun navigateToAddUserDetailsActivity() {
        startActivity(Intent(requireActivity(), AddUserDetailsActivity::class.java))
    }
}
