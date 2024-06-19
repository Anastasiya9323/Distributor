package com.bignerdranch.android.distributor

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listData: ArrayList<String>
    private lateinit var listTemp: ArrayList<UsersHistory>

    private var mDataBase: DatabaseReference? = null
    private val USER_KEY = "UsersHistory"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val email = intent.getStringExtra("email")
        init()
        if (email != null) {
            getDataFromDB(email)
        }
        setOnClickItem()
    }

    private fun init() {
        listView = findViewById(R.id.listView)
        listData = ArrayList()
        listTemp = ArrayList()
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData)
        listView.setAdapter(adapter)
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY)
    }

    private fun getDataFromDB(email: String) {
        val vListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (listData.size > 0) listData.clear()
                if (listTemp.size > 0) listTemp.clear()
                for (ds in dataSnapshot.children) {
                    val history = ds.getValue(
                        UsersHistory::class.java
                    )!!
                    if (history.email == email){
                    listData.add(history.date)
                    listTemp.add(history)}
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        }
        mDataBase!!.addValueEventListener(vListener)
    }

    private fun setOnClickItem() {
        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val history = listTemp!![position]
            val i = Intent(this, ShowActivity::class.java)
            i.putExtra(Constant.USER_DIALOG_COUNT, history.countDialog)
            i.putExtra(Constant.USER_FOLDER_COUNT, history.countFolder)
            i.putExtra(Constant.USER_DATE, history.date)
            startActivity(i)
        })
    }
}