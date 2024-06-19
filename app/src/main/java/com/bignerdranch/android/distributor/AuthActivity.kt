package com.bignerdranch.android.distributor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userLogin: EditText = findViewById(R.id.user_login_auth)
        val userPass: EditText = findViewById(R.id.user_pass_auth)
        val button: Button = findViewById(R.id.button_auth)
        val linkToMain: TextView = findViewById(R.id.link_to_reg)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance()
        val users = db.getReference("Users")

        linkToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener{
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (login == "" || pass =="")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else{
                auth.signInWithEmailAndPassword(login, pass).addOnCompleteListener(this){
                    if (it.isSuccessful) {
                        userLogin.text.clear()
                        userPass.text.clear()

                        Toast.makeText(this, "Пользователь авторизован", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, ItemsActivity::class.java)
                            intent.putExtra("email", login)
                            startActivity(intent)
                        finish()
                    } else
                        Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}