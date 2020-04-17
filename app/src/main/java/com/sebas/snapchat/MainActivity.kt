package com.sebas.snapchat

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if(mAuth.currentUser != null)
        {
            logIn()
        }
    }

    fun goClicked(view: View) {
        //Check if we can log in the user
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        logIn()
                    } else {
                        // If sign in fails, display a message to the user.
                        mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(),passwordEditText?.text.toString()).addOnCompleteListener(this){ task ->
                            if( task.isSuccessful ){
                                // Add to database
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.result.user.uid).child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            } else{
                                Toast.makeText(this, "Log in failed, try again",Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    // ...
                }

        //Sign up a user
    }

    fun logIn(){
        //move to next activity
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}

