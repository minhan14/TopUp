package com.chicohan.mobiletopup.helper

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class PreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveCurrentNumber(ph: String) =
        sharedPreferences.edit { putString("current_phone_number", ph) }

    fun getCurrentNumber(): String? = sharedPreferences.getString("current_phone_number", "")

    fun isFirstRun(): Boolean = sharedPreferences.getBoolean("is_first_run", true)

    fun setFirstRunCompleted() = sharedPreferences.edit { putBoolean("is_first_run", false) }

    fun clean() = sharedPreferences.edit { clear() }

}