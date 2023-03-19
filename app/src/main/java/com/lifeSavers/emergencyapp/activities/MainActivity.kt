package com.lifeSavers.emergencyapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lifeSavers.emergencyapp.databinding.ActivityMainBinding
import com.lifeSavers.emergencyapp.utils.Utils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBar: ActionBar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Home"

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.mapBtn.setOnClickListener {
            startActivity(Intent(this, PermissionsActivity::class.java))
        }

        binding.phoneNumbersBtn.setOnClickListener {
            startActivity(Intent(this, EmergencyPhoneNumbersActivity::class.java))
        }

        binding.assistantsListBtn.setOnClickListener {
            startActivity(Intent(this, AssistantsListForUsersActivity::class.java))
        }

        binding.shareBtn.setOnClickListener {
            Utils().shareButtonFunctionality(this)
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            sharedPreferences = getSharedPreferences("com.lifeSavers.emergencyapp", MODE_PRIVATE)
            if (!sharedPreferences.contains(uid) || sharedPreferences.getBoolean(uid, true)) {
                with(sharedPreferences.edit()) {
                    putBoolean(uid, false)
                    apply()
                }
                startActivity(Intent(this, GuidePage1::class.java))
            }
        }
    }
}
