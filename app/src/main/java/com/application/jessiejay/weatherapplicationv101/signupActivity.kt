package com.application.jessiejay.weatherapplicationv101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class signupActivity : AppCompatActivity() {

    private lateinit var mUsername : EditText
    private lateinit var mPassword : EditText
    private lateinit var mName : EditText
    private lateinit var mSignupButton : Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()
        mUsername = findViewById(R.id.username_edittext_signup)
        mPassword = findViewById(R.id.password_edittext_signup)
        mName = findViewById(R.id.name_edittext_signup)
        mSignupButton = findViewById(R.id.signup_submit_button)

        mSignupButton.setOnClickListener{ view ->
            if(validateInput()) {
                //update database
                val username = mUsername.text.toString().trim()
                val password = mPassword.text.toString().trim()
                firebaseAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this.applicationContext, "Registered successfully",Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this.applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this.applicationContext, "Please enter all information to register", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput() : Boolean {
        return !mUsername.text.isEmpty() && !mPassword.text.isEmpty() && !mName.text.isEmpty()
    }
}
