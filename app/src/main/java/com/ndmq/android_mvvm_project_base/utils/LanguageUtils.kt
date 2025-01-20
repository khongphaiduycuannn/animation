package com.ndmq.android_mvvm_project_base.utils

import android.content.Context
import com.tencent.mmkv.MMKV
import java.util.Locale

const val LANGUAGE: String = "LANGUAGE"

class LanguageUtils(
    private val context: Context
) {

    private val mmkv by lazy {
        MMKV.initialize(context)
        MMKV.defaultMMKV()
    }


    fun setLanguage(language: String) {
        mmkv.encode(LANGUAGE, language)
        applyLanguage()
    }

    fun setLanguage(locale: Locale) {
        val language = locale.language.lowercase()
        setLanguage(language)
    }

    private fun applyLanguage() {
        val language: String = getLanguage()

        val newLocale = Locale(language.lowercase(Locale.getDefault()))
        Locale.setDefault(newLocale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }


    fun getLanguage(): String {
        return mmkv.decodeString(LANGUAGE, null) ?: Locale.getDefault().language
    }

    fun getLocate(): Locale {
        val language = getLanguage()

        val locale = Locale(language.lowercase(Locale.getDefault()))
        return locale
    }
}