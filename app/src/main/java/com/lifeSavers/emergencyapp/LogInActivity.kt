package com.lifeSavers.emergencyapp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lifeSavers.emergencyapp.databinding.ActivityLoginBinding

class LogInActivity : AppCompatActivity() {
    // ViewBinding
    private lateinit var binding: ActivityLoginBinding

    // ActionBar
    private lateinit var actionBar: ActionBar

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    // FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    // Google Sign In
    private lateinit var googleSignInClient: GoogleSignInClient

    private var email = ""
    private var password = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure actionBar
        actionBar = supportActionBar!!
        actionBar.title = "Login"

        // Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        // init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Initialize google sign in client before clicking on the button
        // because there might be some internet issues like
        // internet is slow or any issues like that

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestScopes(Scope(Scopes.PROFILE), Scope(Scopes.EMAIL))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.signInGoogleBtn).setOnClickListener {
            signInGoogle()
        }

        // handle click, open SignUpActivity
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // handle click, open ForgotPasswordActivity
        binding.forgotPasswordTv.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Reset Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val email = view.findViewById<EditText>(R.id.emailEt)
            builder.setView(view)
            builder.setPositiveButton("Reset") { _, _ ->
                forgotPassword(email)
            }
            builder.setNegativeButton("close") { _, _ -> }
            builder.show()
        }

        // handle click, begin login
        binding.loginBtn.setOnClickListener {
            // before logging in, validate data
            validateData()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val googleEmail = account.email
        val googlePic = account.photoUrl

        // check if the email already exists in the database
        val database =
            FirebaseDatabase.getInstance("https://emergencyapp-3a6bd-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")

        database.orderByChild("email").equalTo(googleEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Email already exists, redirect to MainActivity
                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Email does not exist, redirect to GoogleSignUpActivity
                    val intent = Intent(this@LogInActivity, GoogleSignUpActivity::class.java)
                    intent.putExtra("email", googleEmail)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled", error.toException())
                Toast.makeText(this@LogInActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        /////////

        /*
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent : Intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                intent.putExtra("profilePic", account.photoUrl)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
         */
    }

    private fun forgotPassword(email: EditText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            // invalid email format
            return
        }

        firebaseAuth.sendPasswordResetEmail(email.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateData() {
        // get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // invalid email format
            binding.emailEt.error = "Invalid email format"

        } else if (TextUtils.isEmpty(password)) {
            // no password entered
            binding.passwordEt.error = "Please enter password"
        } else {
            // data is validated, begin login
            firebaseLogIn()
        }
    }

    private fun firebaseLogIn() {
        // show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // login success
                progressDialog.dismiss()
                // get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email

                if (firebaseUser.isEmailVerified) {
                    Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()

                    val database =
                        FirebaseDatabase.getInstance("https://emergencyapp-3a6bd-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")
                    val deviceToken =
                        getSharedPreferences("com.lifeSavers.emergencyapp", MODE_PRIVATE).getString(
                            "device_token",
                            null
                        )

                    firebaseUser.uid.let { uid ->
                        database.child(uid).child("deviceToken").setValue(deviceToken)
                            .addOnSuccessListener {
                                Log.d("device_token", "Device token updated successfully")
                            }
                            .addOnFailureListener {
                                Log.d("device_token", "Device token couldn't be updated")
                            }
                    }

                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val userType = dataSnapshot.getValue(Long::class.java)

                            if (userType.toString() == "0") {
                                startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(TAG, "Failed.", error.toException())
                        }
                    }

                    database.child(firebaseUser.uid).child("userType")
                        .addListenerForSingleValueEvent(postListener)
                    // open profile

//                    startActivity(Intent(this, PermissionsActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Please verify your email address.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            .addOnFailureListener { e ->
                // login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        // if user is already logged in go to profile activity
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // user is already logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}