package com.bignerdranch.android.distributor

class UsersHistory {
    lateinit var countDialog: String
    lateinit var countFolder: String
    lateinit var date: String
    lateinit var email: String

    fun UsersHistory() {}

    fun UsersHistory(_countDialog: String, _countFolder: String, _date: String, _email: String) {
        countDialog = _countDialog
        countFolder = _countFolder
        date = _date
        email = _email
    }
}