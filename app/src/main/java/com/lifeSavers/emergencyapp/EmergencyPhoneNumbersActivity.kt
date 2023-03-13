package com.lifeSavers.emergencyapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*

class EmergencyPhoneNumbersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var phoneNumber = "123"

    private val requestPhoneCall = 1

    // ActionBar
    private lateinit var actionBar: ActionBar

    private lateinit var databaseReference: DatabaseReference
    private lateinit var phoneNumbersMap: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_phone_numbers)

        // Configure ActionBar, enable back button
        actionBar = supportActionBar!!
        actionBar.title = "Emergency in Romania"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        listView = findViewById(R.id.listView)

        databaseReference = FirebaseDatabase.getInstance().reference.child("PhoneNumbers")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val names = ArrayList<String>()
                phoneNumbersMap = mutableMapOf()

                for (ds in snapshot.children) {
                    val name = ds.key.toString().removeSurrounding("\"")
                    val phoneNumber = ds.value.toString()

                    names.add(name)
                    phoneNumbersMap[name] = phoneNumber
                }

                val arrayAdapter: ArrayAdapter<String> =
                    ArrayAdapter(
                        this@EmergencyPhoneNumbersActivity,
                        android.R.layout.simple_list_item_1,
                        names
                    )

                listView.adapter = arrayAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val name = listView.getItemAtPosition(position).toString()
            val phoneNumber = phoneNumbersMap[name]

            if (phoneNumber != null) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        requestPhoneCall
                    )
                } else {
                    this.phoneNumber = phoneNumber
                    startCall()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: $phoneNumber")
        startActivity(callIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPhoneCall)
            startCall()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionBar clicked
        return super.onSupportNavigateUp()
    }
}
