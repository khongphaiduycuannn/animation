package com.ndmq.android_mvvm_project_base.utils

import android.content.Context
import com.ndmq.android_mvvm_project_base.R
import com.tencent.mmkv.MMKV

const val THEME = "THEME"

class ThemeUtils(
    private val context: Context
) {

    data class Theme(
        val themeID: Int,
        val styleResource: Int
    )

    companion object {

        val DEFAULT_THEME = Theme(0, R.style.Theme_DefaultTheme)

        val themes: List<Theme> = mutableListOf(DEFAULT_THEME)
    }


    private val mmkv by lazy {
        MMKV.initialize(context)
        MMKV.defaultMMKV()
    }


    private fun getStyleResourceFromID(id: Int): Int {
        if (themes.isEmpty()) return DEFAULT_THEME.styleResource

        val theme = themes.firstOrNull { it.themeID == id } ?: DEFAULT_THEME
        return theme.styleResource
    }

    fun getCurrentThemeID(): Int {
        val currentTheme = mmkv.decodeInt(THEME, DEFAULT_THEME.themeID)
        return getStyleResourceFromID(currentTheme)
    }


    fun getThemeColor(attr: Int, themeID: Int = getCurrentThemeID()): Int {
        val theme = getStyleResourceFromID(themeID)
        val attrs = context.theme.obtainStyledAttributes(theme, intArrayOf(attr))
        val color = attrs.getColor(0, 0)

        attrs.recycle()

        return color
    }

    fun getThemeHexColor(attr: Int, themeID: Int = getCurrentThemeID()): String {
        val theme = getStyleResourceFromID(themeID)
        val attrs = context.theme.obtainStyledAttributes(theme, intArrayOf(attr))
        val hexColor = Integer.toHexString(attrs.getColor(0, 0))

        attrs.recycle()

        return hexColor
    }
}