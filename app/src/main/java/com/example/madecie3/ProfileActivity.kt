package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameText       = findViewById<TextView>(R.id.profileName)
        val emailText      = findViewById<TextView>(R.id.profileEmail)
        val avatarText     = findViewById<TextView>(R.id.profileAvatar)
        val logoutBtn      = findViewById<Button>(R.id.logoutBtn)
        val themeToggleBtn = findViewById<ImageButton>(R.id.themeToggleBtn)
        val auth           = FirebaseAuth.getInstance()
        val currentUser    = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        val displayName = currentUser.displayName ?: "User"
        nameText.text   = displayName
        emailText.text  = currentUser.email ?: "No email"
        avatarText.text = displayName.firstOrNull()?.uppercase() ?: "U"

        // Cache profile data
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("cached_name",  displayName)
            .putString("cached_email", currentUser.email ?: "No email")
            .apply()

        // Theme toggle
        themeToggleBtn.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        logoutBtn.setOnClickListener {
            getSharedPreferences("prefs", MODE_PRIVATE).edit().clear().apply()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}