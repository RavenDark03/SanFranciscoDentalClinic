package com.example.sanfranciscodentalclinic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.sanfranciscodentalclinic.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle

    // Firebase Southeast Asia Region URL
    private val DB_URL = "https://dental-clinic-f32da-default-rtdb.asia-southeast1.firebasedatabase.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // --- SIDEBAR BUTTON SETUP ---
        setSupportActionBar(binding.toolbar) // Sets Toolbar as Action Bar
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState() // CRITICAL: This physically displays the sidebar button

        fetchDashboardData()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> { /* Stay here */ }
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun fetchDashboardData() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance(DB_URL).getReference("Users").child(uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("fullName").value?.toString() ?: "User"
                binding.tvWelcomeUser.text = "Hello, ${name.lowercase()} 👋"

                // Update Cards
                binding.tvBalance.text = "₱${snapshot.child("pendingBalance").value ?: "0"}"
                binding.tvTotalVisits.text = snapshot.child("totalVisits").value?.toString() ?: "0"
                binding.tvUpcomingProcedure.text = snapshot.child("upcomingProcedure").value?.toString() ?: "No Upcoming Visit"

                // Update Sidebar Header
                val headerView = binding.navView.getHeaderView(0)
                headerView.findViewById<TextView>(R.id.tvHeaderName).text = name
                headerView.findViewById<TextView>(R.id.tvHeaderEmail).text = auth.currentUser?.email
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}