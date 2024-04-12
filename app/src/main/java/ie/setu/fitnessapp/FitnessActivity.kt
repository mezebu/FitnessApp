package ie.setu.fitnessapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.setu.fitnessapp.activities.AddFitnessActivity
import ie.setu.fitnessapp.databinding.ActivityMainBinding
import timber.log.Timber
import timber.log.Timber.i

class FitnessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("Fitness Journal Activity started..")

        binding.fab.setOnClickListener {
            navigateToAddFitness()
        }
    }

    private fun navigateToAddFitness() {
        val intent = Intent(this, AddFitnessActivity::class.java)
        startActivity(intent)
    }

}