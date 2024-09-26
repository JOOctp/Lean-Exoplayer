package com.jop.task.local

import android.content.Context

class AppData(context: Context) {
    private val PREFS_NAME = "LOCAL_DATA"
    private val TOKEN = "TOKEN"

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setToken(token: String){
        prefs.edit().putString(TOKEN, token).apply()
    }

    fun getToken() = prefs.getString(TOKEN, "")
}