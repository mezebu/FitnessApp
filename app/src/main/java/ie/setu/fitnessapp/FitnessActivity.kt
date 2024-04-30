package ie.setu.fitnessapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import ie.setu.fitnessapp.activities.LoginActivity
import ie.setu.fitnessapp.databinding.ActivityMainBinding
import ie.setu.fitnessapp.fragments.HomeFragment
import ie.setu.fitnessapp.fragments.ProfileFragment
import timber.log.Timber
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class FitnessActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        Timber.i("Fitness Activity started..")

        setupDrawerAndToolbar()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.navView.setCheckedItem(R.id.navHome)
        }

        updateNavHeader()
    }

    private fun setupDrawerAndToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = binding.drawerLayout
        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    @SuppressLint("SetTextI18n")
    private fun updateNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val navHeaderUsername = headerView.findViewById<TextView>(R.id.navHeaderUsername)
        val navHeaderEmail = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        val user = firebaseAuth.currentUser

        if (user != null) {
            // Set the email directly from the FirebaseAuth instance
            navHeaderEmail.text = user.email ?: "No Email"

            // Fetch the username from Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Set the username from the document
                        navHeaderUsername.text = document.getString("username") ?: "No Username"
                    } else {
                        // Handle case where there is no such document
                        navHeaderUsername.text = "No Username"
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    navHeaderUsername.text = "Error Loading Username"
                }
        } else {
            // If no user is logged in, set default text
            navHeaderUsername.text = "No Username"
            navHeaderEmail.text = "No Email"
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navHome -> replaceFragment(HomeFragment())
            R.id.navProfile -> replaceFragment(ProfileFragment())
            R.id.btnLogout -> logOutUser()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logOutUser() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
