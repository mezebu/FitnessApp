package ie.setu.fitnessapp.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

object FirebaseImageManager {
    private val storage = FirebaseStorage.getInstance().reference
    var imageUri = MutableLiveData<Uri>()

    fun checkStorageForExistingProfilePic(userid: String) {
        val imageRef = storage.child("photos").child("$userid.jpg")
        imageRef.metadata.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                imageUri.value = uri
            }
        }.addOnFailureListener {
            imageUri.value = Uri.EMPTY
        }
    }

    fun uploadImageToFirebase(userid: String, bitmap: Bitmap, updating: Boolean) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imageRef = storage.child("photos").child("$userid.jpg")

        imageRef.putBytes(data).addOnSuccessListener {
            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                imageUri.value = uri
            }
        }
    }
}
