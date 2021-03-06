package com.auditionstreet.artist.storage.preference

import android.content.Context
import android.content.SharedPreferences
import com.auditionstreet.artist.utils.AppConstants
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Preferences @Inject constructor(@ApplicationContext context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCE_CASTING, Context.MODE_PRIVATE)

    fun setString(key: String, value: String?) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    fun setBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }
    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun <T> put(key: String,`object`: T) {
        val jsonString = GsonBuilder().create().toJson(`object`)
        sharedPreferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> get(key: String): T? {
        val value = sharedPreferences.getString(key, "")
        return GsonBuilder().create().fromJson(value, T::class.java)
    }
}
