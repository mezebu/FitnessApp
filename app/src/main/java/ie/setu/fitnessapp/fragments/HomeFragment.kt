import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ie.setu.fitnessapp.R
import ie.setu.fitnessapp.activities.AddFitnessActivity

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the FloatingActionButton by its ID
        val fabButton: FloatingActionButton = view.findViewById(R.id.fab)

        // Set click listener for the FloatingActionButton
        fabButton.setOnClickListener {
            navigateToAddFitness()
        }

        return view
    }

    private fun navigateToAddFitness() {
        val intent = Intent(activity, AddFitnessActivity::class.java)
        startActivity(intent)
    }
}
