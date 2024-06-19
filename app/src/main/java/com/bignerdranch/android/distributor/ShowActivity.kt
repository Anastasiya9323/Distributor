package com.bignerdranch.android.distributor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ShowActivity : AppCompatActivity() {
    private lateinit var dialogCount: TextView
    private lateinit var folderCount:TextView
    private lateinit var date:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        init()
        getIntentMain()
    }
    private fun init() {
        dialogCount = findViewById(R.id.tvName)
        folderCount = findViewById(R.id.tvSecName)
        date = findViewById(R.id.tvEmail)
    }

    private fun getIntentMain() {
        val i = intent
        if (i != null) {
            dialogCount.setText("Распределено диалогов: "+i.getStringExtra(Constant.USER_DIALOG_COUNT))
            folderCount.setText("Количество папок: "+i.getStringExtra(Constant.USER_FOLDER_COUNT))
            date.setText("Дата распределения: "+ i.getStringExtra(Constant.USER_DATE))
        }
    }
}