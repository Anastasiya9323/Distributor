package com.bignerdranch.android.distributor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.util.*

class AccActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)

        val email = intent.getStringExtra("emailString")
        if (email != null) {
            Log.d("mytag",email)
        } else Log.d("mytag", "email null     help me")

        val countButton: Button = findViewById(R.id.count_button)
        val countText: TextView = findViewById(R.id.count_text)
        val putFolderButton: Button = findViewById(R.id.put_button)
        val resText: TextView = findViewById(R.id.result_text)
        val folderText: TextView = findViewById(R.id.folder_text)
        val historyLink: TextView = findViewById(R.id.history_link2)

        val chatCheckBox: CheckBox = findViewById(R.id.checkBoxChats)
        val channelCheckBox: CheckBox = findViewById(R.id.checkBoxChannels)

        val chatLayout: LinearLayout = findViewById(R.id.chat_layout)
        val channelLayout: LinearLayout = findViewById(R.id.channel_layout)

        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        historyLink.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        countButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (! Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }
            val python = Python.getInstance()

            val list = python.getModule("main")
                .callAttr("get_dialogs").asList()

            Handler(Looper.getMainLooper()).postDelayed({
                progressBar.visibility = View.GONE
                countText.setText("Всего диалогов: ${list[0]}. \nКоличество папок: ${list[1]}. \nРаспределено: ${list[2]}. \nНе распределено: ${list[3]}.")
                chatLayout.visibility = View.VISIBLE
                channelLayout.visibility = View.VISIBLE
                putFolderButton.visibility = View.VISIBLE
            }, 1000)
            countText.visibility = View.VISIBLE

            folderText.setText(list[1].toString())
        }

        putFolderButton.setOnClickListener {

            val chatFlag = chatCheckBox.isChecked
            val channelFlag = channelCheckBox.isChecked
            if (!chatFlag and !channelFlag){
                Toast.makeText(this, "Не отмечено что нужно распределить", Toast.LENGTH_LONG).show()
            } else {
                if (! Python.isStarted()) {
                Python.start(AndroidPlatform(this))
                }
                val python = Python.getInstance()

                val countDialogs = python.getModule("main")
                    .callAttr("put_dialogs", chatFlag, channelFlag).toString()

                val firebaseHelper = FirebaseHelper()
                if (email != null) {
                    firebaseHelper.saveDataToUsersHistory(email, countDialogs, folderText.text.toString().trim(), Date().toString())
                }

                resText.visibility = View.VISIBLE

                resText.setText("Диалоги распределены!")
            }
        }
    }
}
