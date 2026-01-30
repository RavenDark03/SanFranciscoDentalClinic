package com.example.sanfranciscodentalclinic

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sanfranciscodentalclinic.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "DentalClinic_Debug" // Tag for Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Validation Algorithm
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all dental records", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Step 1: Starting Auth for $email")

        // 2. Create User in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d(TAG, "Step 2: Auth Successful. UID: $userId")

                    // 3. Pass details to Realtime Database
                    saveUserToDatabase(userId, name, email, phone)
                } else {
                    Log.e(TAG, "Step 2 Failed: ${task.exception?.message}")
                    Toast.makeText(this, "Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(uid: String?, name: String, email: String, phone: String) {
        if (uid == null) return

        val database = FirebaseDatabase.getInstance().getReference("Patients")
        val userObject = User(uid, name, email, phone)

        Log.d(TAG, "Step 3: Sending data object to Firebase...")

        database.child(uid).setValue(userObject)
            .addOnSuccessListener {
                Log.d(TAG, "Step 4: Database Write SUCCESS")
                Toast.makeText(this, "Welcome to SF Dental Clinic!", Toast.LENGTH_LONG).show()
                finish() // Go back to Login
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Step 4 Failed: ${e.message}")
                Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
