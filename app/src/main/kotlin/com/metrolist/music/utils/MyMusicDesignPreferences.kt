package com.metrolist.music.utils

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.metrolist.music.constants.*
import com.metrolist.music.ui.screens.settings.DarkMode

private val DesignApplied = booleanPreferencesKey("myMusicLimeDesignApplied")

fun applyMyMusicDesign(preferences: MutablePreferences) {
    if (preferences[DesignApplied] == true) return
    preferences[DarkModeKey] = DarkMode.ON.name
    preferences[SelectedThemeColorKey] = 0xFFC7DE5A.toInt()
    preferences[DynamicThemeKey] = false
    preferences[PureBlackKey] = false
    preferences[SlimNavBarKey] = true
    preferences[UseNewPlayerDesignKey] = true
    preferences[PlayerBackgroundStyleKey] = PlayerBackgroundStyle.BLUR.name
    preferences[AlbumViewTypeKey] = LibraryViewType.LIST.name
    preferences[PlaylistViewTypeKey] = LibraryViewType.LIST.name
    preferences[DesignApplied] = true
}
