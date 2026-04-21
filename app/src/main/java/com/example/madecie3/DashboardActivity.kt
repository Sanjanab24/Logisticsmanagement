package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val createBtn   = findViewById<LinearLayout>(R.id.createShipmentBtn)
        val shipmentsBtn = findViewById<LinearLayout>(R.id.shipmentsBtn)
        val trackBtn    = findViewById<LinearLayout>(R.id.trackBtn)
        val ordersBtn   = findViewById<LinearLayout>(R.id.ordersBtn)
        val profileBtn  = findViewById<LinearLayout>(R.id.profileBtn)
        val recycler    = findViewById<RecyclerView>(R.id.productsRecycler)
        val progressBar = findViewById<ProgressBar>(R.id.dashboardProgress)
        val errorText   = findViewById<TextView>(R.id.dashboardError)
        val greeting    = findViewById<TextView>(R.id.dashGreeting)
        val themeToggleBtn = findViewById<ImageButton>(R.id.themeToggleBtn)

        // Greeting based on time of day
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        greeting.text = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }

        // Theme toggle
        themeToggleBtn.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        createBtn.setOnClickListener    { startActivity(Intent(this, CreateShipmentActivity::class.java)) }
        shipmentsBtn.setOnClickListener { startActivity(Intent(this, ShipmentsActivity::class.java)) }
        trackBtn.setOnClickListener     { startActivity(Intent(this, TrackShipmentActivity::class.java)) }
        ordersBtn.setOnClickListener    { startActivity(Intent(this, OrdersActivity::class.java)) }
        profileBtn.setOnClickListener   { startActivity(Intent(this, ProfileActivity::class.java)) }
        
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.aiAssistantFab).setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java))
        }

        recycler.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    recycler.adapter = ProductAdapter(products, this@DashboardActivity)
                    errorText.visibility = View.GONE
                } else {
                    errorText.text = "Failed to load products."
                    errorText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                errorText.text = "Network error: ${e.message}"
                errorText.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}