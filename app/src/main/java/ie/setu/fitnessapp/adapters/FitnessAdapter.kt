package ie.setu.fitnessapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.models.FitnessDataModel

class FitnessAdapter(private val fitnessList: List<FitnessDataModel>): RecyclerView.Adapter<FitnessAdapter.FitnessViewHolder>() {
    class FitnessViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var exerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        var duration: TextView = itemView.findViewById(R.id.tvDuration)
        var weight: TextView = itemView.findViewById(R.id.tvWeight)
        var calories: TextView = itemView.findViewById(R.id.tvCalories)
        var endDate: TextView = itemView.findViewById(R.id.tvEndDate)
        var desc: TextView = itemView.findViewById(R.id.tvNote)
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
        holder.exerciseName.text = currentItem.exerciseName
        holder.duration.text = currentItem.duration
        holder.weight.text = currentItem.weight
        holder.calories.text = currentItem.calories
        holder.endDate.text = currentItem.endDate
        holder.desc.text = currentItem.note
    }
}