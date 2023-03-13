package com.lifeSavers.emergencyapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.res.Configuration
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lifeSavers.emergencyapp.adapter.UserAdapter
import com.lifeSavers.emergencyapp.databinding.ActivityAssistantsListForUsersBinding
import com.lifeSavers.emergencyapp.model.User
import java.util.*
import kotlin.collections.ArrayList

class AssistantsListForUsersActivity : AppCompatActivity() {

    var binding: ActivityAssistantsListForUsersBinding? = null
    private var database: FirebaseDatabase? = null
    private var searchView: SearchView? = null
    var users: ArrayList<User>? = null
    var usersAdapter: UserAdapter? = null
    private var dialog: ProgressDialog? = null
    var user: User? = null

    // ActionBar
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistantsListForUsersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Configure ActionBar, enable back button
        actionBar = supportActionBar!!
        actionBar.title = "Assistants"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        dialog = ProgressDialog(this@AssistantsListForUsersActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        database =
            FirebaseDatabase.getInstance("https://emergencyapp-3a6bd-default-rtdb.europe-west1.firebasedatabase.app/")
        users = ArrayList()
        usersAdapter = UserAdapter(this@AssistantsListForUsersActivity, users!!)

        val layoutManagerPortrait = GridLayoutManager(this@AssistantsListForUsersActivity, 2)
        val layoutManagerLandscape = GridLayoutManager(this@AssistantsListForUsersActivity, 3)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding!!.mRec.layoutManager = layoutManagerLandscape
        } else {
            binding!!.mRec.layoutManager = layoutManagerPortrait
        }
        database!!.reference.child("Users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        binding!!.mRec.adapter = usersAdapter

        searchView = findViewById(R.id.searchView)

        searchView!!.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText?.lowercase())
                return true
            }

        })

        database!!.reference.child("Users").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for (snapshot1 in snapshot.children) {
                    val user: User? = snapshot1.getValue(User::class.java)
                    if ((!user!!.email.equals(FirebaseAuth.getInstance().currentUser?.email)) and (user.userType == 1L)) {
                        users!!.add(user)
                    }
                    usersAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<User>()
            for (i in users!!) {
                if (i.name?.toLowerCase(Locale.ROOT)!!.contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            } else {
                usersAdapter!!.setFilteredList(filteredList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId!!).setValue("Offline")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionBar clicked
        return super.onSupportNavigateUp()
    }
}