package ie.setu.fitnessapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.models.FitnessDataModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Interface to handle item click events in the RecyclerView
interface OnFitnessItemClickListener {
    fun onItemClick(fitnessData: FitnessDataModel, documentId: String)
    fun onDeleteClick(fitnessData: FitnessDataModel, documentId: String)
}

// Adapter class for the RecyclerView to display fitness data
class FitnessAdapter(
    private val fitnessList: List<FitnessDataModel>, // List of fitness data
    private val context: Context, // Context of the activity or fragment
    private val listener: OnFitnessItemClickListener, // Listener for item click events
) : RecyclerView.Adapter<FitnessAdapter.FitnessViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class FitnessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in the item layout
        var exerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        var duration: TextView = itemView.findViewById(R.id.tvDuration)
        var weight: TextView = itemView.findViewById(R.id.tvWeight)
        var calories: TextView = itemView.findViewById(R.id.tvCalories)
        var endDate: TextView = itemView.findViewById(R.id.tvEndDate)
        var desc: TextView = itemView.findViewById(R.id.tvNote)
        var timestamp: TextView = itemView.findViewById(R.id.tvTimeStamp)
        var deleteButton: Button = itemView.findViewById(R.id.btnDelete)
    }

    // Create a new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FitnessViewHolder {
        // Inflate the item layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return FitnessViewHolder(itemView).apply {
            // Set OnClickListener for the whole item
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Call onItemClick function of the listener when item is clicked
                    listener.onItemClick(fitnessList[position], fitnessList[position].documentId)
                }
            }

            // Set OnClickListener for the delete button
            deleteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Call onDeleteClick function of the listener when delete button is clicked
                    listener.onDeleteClick(fitnessList[position], fitnessList[position].documentId)
                }
            }
        }
    }

    // Return the number of items in the RecyclerView
    override fun getItemCount(): Int = fitnessList.size

    // Bind data to the views in each ViewHolder
    override fun onBindViewHolder(holder: FitnessViewHolder, position: Int) {
        val currentItem = fitnessList[position]
        // Bind fitness data to respective TextViews
        holder.exerciseName.text = context.getString(R.string.exercise_name_label, currentItem.exerciseName)
        holder.duration.text = context.getString(R.string.duration_label, currentItem.duration)
        holder.weight.text = context.getString(R.string.weight_label, currentItem.weight)
        holder.calories.text = context.getString(R.string.calories_label, currentItem.calories)
        holder.endDate.text = context.getString(R.string.end_date_label, currentItem.endDate)
        holder.desc.text = context.getString(R.string.note_label, currentItem.note)
        holder.timestamp.text = formatDate(currentItem.timestamp) // Format and set timestamp
    }

    // Function to format timestamp to a readable date format
    private fun formatDate(timestamp: Timestamp?): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return timestamp?.toDate()?.let { sdf.format(it) } ?: "No date"
    }
}
