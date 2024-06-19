package com.bignerdranch.android.distributor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.log


class ItemsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        val email = intent.getStringExtra("email")

        val button_access: Button = findViewById(R.id.button_put)
        val userCode: EditText = findViewById(R.id.user_code)
        val button_put: Button = findViewById(R.id.button_folder)
        val helpText: TextView = findViewById(R.id.help_text)
        val historyLink: TextView = findViewById(R.id.history_link)

        val textPhone: TextView = findViewById(R.id.text_phone)
        val textEmail: TextView = findViewById(R.id.text_email)

        val progress: ProgressBar = findViewById(R.id.progBar)

        historyLink.setOnClickListener {
            val intentHistory = Intent(this, HistoryActivity::class.java)
            intentHistory.putExtra("email", email)
            startActivity(intentHistory)
        }

        val database = Firebase.database
        val ref = database.getReference("Users")

        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val phoneValue = userSnapshot.child("phone").value.toString()
                        textPhone.text = phoneValue
                        textEmail.text = email
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase error", "Error getting phone number", databaseError.toException())
            }
        })

        button_access.setOnClickListener{
            if (! Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
            val python = Python.getInstance()

            val phone = textPhone.text.toString().trim()

            val code_hash = python.getModule("main")
                .callAttr("send_phone", phone).toString()

            Handler(Looper.getMainLooper()).postDelayed({
                progress.visibility = View.GONE
            }, 3000)
            userCode.visibility = View.VISIBLE
            button_put.visibility = View.VISIBLE
            helpText.text = code_hash
        }

        button_put.setOnClickListener{
            val code = userCode.text.toString().trim()
            val code_hash = helpText.text.toString().trim()
            val phone = textPhone.text.toString().trim()
            val emailString = textEmail.text.toString().trim()
            if (code != "" && code.length == 5){

                progress.visibility = View.VISIBLE

                if (! Python.isStarted()) {
                    Python.start(AndroidPlatform(this))
                }
                val python = Python.getInstance()
                python.getModule("main")
                    .callAttr("get_access", phone, code, code_hash)

                Handler(Looper.getMainLooper()).postDelayed({
                    progress.visibility = View.GONE
                }, 3000)

                val intentAcc = Intent(this, AccActivity::class.java)
                intentAcc.putExtra("emailString", emailString)
                startActivity(intentAcc)
                finish()
            } else {
                Toast.makeText(this,"Введите код", Toast.LENGTH_LONG).show()
            }
            }
    }

}