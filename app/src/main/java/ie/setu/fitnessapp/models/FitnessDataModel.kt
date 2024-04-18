package ie.setu.fitnessapp.models

import com.google.firebase.Timestamp

data class FitnessDataModel(
    var exerciseName: String = "",
    var duration: String = "",
    var weight: String = "",
    var calories: String = "",
    var endDate: String = "",
    var note: String = "",
    var timestamp: Timestamp? = null
)
