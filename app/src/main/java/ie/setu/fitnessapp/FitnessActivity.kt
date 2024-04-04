package ie.setu.fitnessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import timber.log.Timber
import timber.log.Timber.i

class FitnessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Timber.plant(Timber.DebugTree())

        i("Fitness Journal Activity started..")
    }
}