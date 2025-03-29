package com.chicohan.mobiletopup.helper

import android.app.AlertDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Date
import java.util.Locale
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.invisible(invisible: Boolean) {
    visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.createGenericAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    callback: (Boolean) -> Unit
) {
    val builder = AlertDialog.Builder(this)
    builder.apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, _ ->
            dialog.dismiss()
            callback(true)

        }
        setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.dismiss()
            callback(false)
        }
    }

    val alertDialog = builder.create()
    alertDialog.show()
}

/**
 * etx function to collect flow in different states
 */

fun <T> Fragment.collectFlowWithLifeCycleAtStateStart(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }
}

fun <T> Fragment.collectFlowWithLifeCycleAtStateResume(
    flow: Flow<T>,
    collect: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            flow.collect(collect)
        }
    }
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializable(key) as? T
    }

fun <T : ViewBinding> DialogFragment.viewBinding(
    bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T
): ReadOnlyProperty<DialogFragment, T> = object : ReadOnlyProperty<DialogFragment, T> {
    private var binding: T? = null

    override fun getValue(thisRef: DialogFragment, property: KProperty<*>): T {
        if (binding == null) {
            binding = bindingInflater(layoutInflater, null, false)
        }
        return binding!!
    }
}

fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun String.isValidPhoneNumber(): Boolean {
    val regex = "^(09|\\+959)\\d{7,9}$".toRegex()
    return matches(regex)
}
val ProviderType.logo: Int
    get() = when (this) {
        ProviderType.ATOM -> R.drawable.atom_logo
        ProviderType.MPT -> R.drawable.mpt_logo
        ProviderType.OOREDOO -> R.drawable.ooredoo_logo
        ProviderType.UNKNOWN -> 0
    }

