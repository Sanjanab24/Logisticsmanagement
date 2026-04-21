package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load

class ShipmentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_details)

        val detailText      = findViewById<TextView>(R.id.detailText)
        val image           = findViewById<ImageView>(R.id.detailImage)
        val imagePlaceholder = findViewById<TextView>(R.id.detailImagePlaceholder)
        val detailCategory  = findViewById<TextView>(R.id.detailCategory)
        val detailTitle     = findViewById<TextView>(R.id.detailTitle)
        val detailPrice     = findViewById<TextView>(R.id.detailPrice)
        val detailShipId    = findViewById<TextView>(R.id.detailShipmentId)
        val shipThisBtn     = findViewById<Button>(R.id.shipThisBtn)

        val productId    = intent.getIntExtra("productId", -1)
        val productTitle = intent.getStringExtra("productTitle")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productCat   = intent.getStringExtra("productCategory")
        val productImg   = intent.getStringExtra("productImage")
        val productDesc  = intent.getStringExtra("productDescription")

        val cartId    = intent.getIntExtra("cartId", -1)
        val userId    = intent.getIntExtra("userId", -1)
        val itemCount = intent.getIntExtra("itemCount", 0)

        if (productId != -1 && productTitle != null) {
            // Product detail mode
            detailCategory.text = productCat?.replaceFirstChar { it.uppercase() } ?: "Product"
            detailTitle.text = productTitle
            detailPrice.text = "$${String.format("%.2f", productPrice)}"
            detailShipId.text = "#${productId.toString().padStart(4, '0')}"
            detailText.text = productDesc ?: "No description available."

            if (!productImg.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                imagePlaceholder.visibility = View.GONE
                image.load(productImg) { crossfade(true) }
            } else {
                imagePlaceholder.visibility = View.VISIBLE
                image.visibility = View.GONE
            }

            shipThisBtn.setOnClickListener {
                val intent = Intent(this, CreateShipmentActivity::class.java)
                startActivity(intent)
            }

        } else if (cartId != -1) {
            // Cart/order mode
            detailCategory.text = "ORDER"
            detailTitle.text = "Cart #$cartId"
            detailPrice.text = "$itemCount items"
            detailShipId.text = "#${cartId.toString().padStart(4, '0')}"
            detailText.text = "User ID: $userId\nItems: $itemCount\nStatus: In Transit\nOrigin: Distribution Center\nDestination: User $userId's Address\nETA: 2–5 business days"

            imagePlaceholder.visibility = View.VISIBLE
            image.visibility = View.GONE

            shipThisBtn.text = "GO TO DASHBOARD"
            shipThisBtn.setOnClickListener {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        } else {
            val id = intent.getStringExtra("id")
            detailCategory.text = "SHIPMENT"
            detailTitle.text = "Tracking ID: $id"
            detailPrice.text = "In Transit"
            detailShipId.text = id ?: "N/A"
            detailText.text = "Status: In Transit\nFrom: Bangalore\nTo: Chennai\nPayment: Paid"
            imagePlaceholder.visibility = View.VISIBLE
            image.visibility = View.GONE
            shipThisBtn.visibility = View.GONE
        }
    }
}