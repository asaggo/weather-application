package com.application.jessiejay.weatherapplicationv101

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
                        sendUserData()
                        sendEmailVerification()
                        firebaseAuth.signOut()
                        finish()
                        val intent = Intent(this,loginActivity::class.java)
                        startActivity(intent)
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

    private fun sendUserData(){
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference(firebaseAuth.uid!!)
        val user = User(mName.text.toString())
        reference.setValue(user)
    }

    private fun sendEmailVerification(){
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser!=null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this,getString(R.string.registered_verification_email_sent), Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this,getString(R.string.verification_email_not_sent), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
