package com.bignerdranch.android.distributor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        val userEmail: EditText = findViewById(R.id.user_email)
        val userLogin: EditText = findViewById(R.id.user_login)
        val userPhone: EditText = findViewById(R.id.user_phone)
        val userPass: EditText = findViewById(R.id.user_pass)
        val button: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance()
        val users = db.getReference("Users")

        linkToAuth.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener{
            val email = userEmail.text.toString().trim()
            val login = userLogin.text.toString().trim()
            val phone = userPhone.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (email == "" || login == "" || phone == "" || pass =="")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else{
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this){
                    if (it.isSuccessful) {
                        val user = User(email, login, phone, pass)

                        FirebaseAuth.getInstance()
                            .currentUser?.let { it1 -> users.child(it1.uid).setValue(user) }
                        Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_SHORT).show()

                        userEmail.text.clear()
                        userLogin.text.clear()
                        userPhone.text.clear()
                        userPass.text.clear()
                    } else {
                        Toast.makeText(this, "Пользователь не добавлен", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}