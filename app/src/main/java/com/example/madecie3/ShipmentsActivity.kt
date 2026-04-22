package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.data.AppDatabase
import kotlinx.coroutines.launch

class ShipmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipments)

        val recyclerView = findViewById<RecyclerView>(R.id.shipmentsRecyclerView)
        val countText    = findViewById<TextView>(R.id.shipmentCountText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@ShipmentsActivity)
            val shipments = db.shipmentDao().getAllShipments()
            
            countText.text = "${shipments.size} Total"
            recyclerView.adapter = ShipmentAdapter(shipments)
        }
    }
}
