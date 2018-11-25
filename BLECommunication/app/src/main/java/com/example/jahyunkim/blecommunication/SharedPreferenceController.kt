package com.example.jahyunkim.blecommunication

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceController{
    private val Counter: String = "counter"

    fun setCounter(ctx: Context, counter: Int){
        val preferences: SharedPreferences = ctx.getSharedPreferences("Scanner", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt(Counter, counter)
        editor.commit()
    }

    fun getCounter(ctx: Context): Int {
        val preferences: SharedPreferences = ctx.getSharedPreferences("Scanner", Context.MODE_PRIVATE)
        return preferences.getInt(Counter, 0)
    }
}