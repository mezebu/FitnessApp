package ie.setu.fitnessapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ie.setu.fitnessapp.FitnessActivity
import ie.setu.fitnessapp.databinding.ActivityAddUserDetailsBinding
import java.io.ByteArrayOutputStream

class AddUserDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityAddUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSaveDetails.setOnClickListener {
            uploadImageThenSaveDetails()
        }
    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.imageViewUserPhoto.setImageURI(it)
                imageUri = it
            }
        }

    private fun uploadImageThenSaveDetails() {
        imageUri?.let {
            val ref = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
            val imageView = binding.imageViewUserPhoto
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                ref.putBytes(data).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { url ->
                        saveUserDetails(url.toString())
                    }
                }.addOnFailureListener {
                    showSnackbar("Failed to upload image: ${it.message}")
                }
            } else {
                showSnackbar("Invalid image format for upload")
            }
        } ?: run {
            saveUserDetails(null)
        }
    }

    private fun saveUserDetails(imageUrl: String?) {
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
            "timestamp" to FieldValue.serverTimestamp(),
            "imageUrl" to imageUrl
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
