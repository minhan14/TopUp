package com.chicohan.mobiletopup.helper

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class PreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_CURRENT_PHONE_NUMBER = "current_phone_number"
        private const val KEY_IS_FIRST_RUN = "is_first_run"
    }

    fun saveCurrentNumber(ph: String) =
        sharedPreferences.edit { putString(KEY_CURRENT_PHONE_NUMBER, ph) }

    fun getCurrentNumber(): String? = sharedPreferences.getString(KEY_CURRENT_PHONE_NUMBER, "")

    fun isFirstRun(): Boolean = sharedPreferences.getBoolean(KEY_IS_FIRST_RUN, true)

    fun setFirstRunCompleted() = sharedPreferences.edit { putBoolean(KEY_IS_FIRST_RUN, false) }

    fun clearPhoneNumber() = sharedPreferences.edit { remove(KEY_CURRENT_PHONE_NUMBER) }

    fun clean() = sharedPreferences.edit { clear() }

}