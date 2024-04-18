package ie.setu.fitnessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.models.FitnessDataModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FitnessAdapter(private val fitnessList: List<FitnessDataModel>, private val context: Context): RecyclerView.Adapter<FitnessAdapter.FitnessViewHolder>() {
    class FitnessViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var exerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        var duration: TextView = itemView.findViewById(R.id.tvDuration)
        var weight: TextView = itemView.findViewById(R.id.tvWeight)
        var calories: TextView = itemView.findViewById(R.id.tvCalories)
        var endDate: TextView = itemView.findViewById(R.id.tvEndDate)
        var desc: TextView = itemView.findViewById(R.id.tvNote)
        var timestamp: TextView = itemView.findViewById(R.id.tvTimeStamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FitnessViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return FitnessViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return fitnessList.size
    }

    override fun onBindViewHolder(holder: FitnessViewHolder, position: Int) {
        val currentItem = fitnessList[position]
        holder.exerciseName.text = context.getString(R.string.exercise_name_label, currentItem.exerciseName)
        holder.duration.text = context.getString(R.string.duration_label, currentItem.duration)
        holder.weight.text = context.getString(R.string.weight_label, currentItem.weight)
        holder.calories.text = context.getString(R.string.calories_label, currentItem.calories)
        holder.endDate.text = context.getString(R.string.end_date_label, currentItem.endDate)
        holder.desc.text = context.getString(R.string.note_label, currentItem.note)
        holder.timestamp.text = formatDate(currentItem.timestamp)
    }

    private fun formatDate(timestamp: Timestamp?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()
            sdf.format(timestamp.toDate())
        } else {
            "No date"
        }
    }
}