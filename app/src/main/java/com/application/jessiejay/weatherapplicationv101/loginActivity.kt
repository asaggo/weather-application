package com.application.jessiejay.weatherapplicationv101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class loginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mLoginButton: Button
    private lateinit var mSignupButton: Button
    private lateinit var mUsernameInput: EditText
    private lateinit var mPasswordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUsernameInput = findViewById(R.id.username_edittext)
        mPasswordInput = findViewById(R.id.password_edittext)
        mLoginButton = findViewById(R.id.login_button)
        mSignupButton = findViewById(R.id.signup_button)
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser
        if(user != null){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        mLoginButton.setOnClickListener {
            if(validateInput()){
                validateUser()
            }
        }
        mSignupButton.setOnClickListener {
            val intent = Intent(applicationContext, signupActivity::class.java)
            startActivity(intent)
        }
    }

    fun validateInput() :Boolean{
        return !mUsernameInput.text.isEmpty() && !mPasswordInput.text.isEmpty()
    }
    fun validateUser() {
        val username = mUsernameInput.text.toString().trim()
        val password = mPasswordInput.text.toString().trim()
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if(task.isSuccessful && checkEmailVerification()){
                Toast.makeText(applicationContext,"Login success", Toast.LENGTH_SHORT).show()
                finish()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            } else if (task.isSuccessful && !checkEmailVerification()){
                Toast.makeText(applicationContext, getString(R.string.verify_your_email),Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
            } else{
                Toast.makeText(applicationContext,"Login failed. Please check your username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmailVerification():Boolean{
        return firebaseAuth.currentUser!!.isEmailVerified
    }
}
