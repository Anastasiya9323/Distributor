package com.bignerdranch.android.distributor

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    private val database = FirebaseDatabase.getInstance()
    private val usersHistoryRef = database.getReference("UsersHistory")

    fun saveDataToUsersHistory(email: String, countDialog: String, countFolder: String, date: String) {
        val userHistoryData = mapOf(
            "email" to email,
            "countDialog" to countDialog,
            "countFolder" to countFolder,
            "date" to date
        )
        val userHistoryId = usersHistoryRef.push().key
        usersHistoryRef.child(userHistoryId!!).setValue(userHistoryData)
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Data saved to UsersHistory successfully")
            }
            .addOnFailureListener {
                Log.e("FirebaseHelper", "Error saving data to UsersHistory: ${it.message}")
            }
    }
}
