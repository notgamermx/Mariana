package com.metrolist.music.utils

import androidx.datastore.preferences.core.mutablePreferencesOf
import com.metrolist.music.constants.*
import com.metrolist.music.ui.screens.settings.DarkMode
import org.junit.Assert.*
import org.junit.Test

class MyMusicDesignPreferencesTest {
    @Test fun appliesAppearanceWithoutChangingAccountData() {
        val preferences = mutablePreferencesOf(InnerTubeCookieKey to "existing-session")
        applyMyMusicDesign(preferences)
        assertEquals(DarkMode.ON.name, preferences[DarkModeKey])
        assertEquals(LibraryViewType.LIST.name, preferences[PlaylistViewTypeKey])
        assertEquals("existing-session", preferences[InnerTubeCookieKey])
    }
    @Test fun preservesLaterUserCustomization() {
        val preferences = mutablePreferencesOf()
        applyMyMusicDesign(preferences)
        preferences[DarkModeKey] = DarkMode.OFF.name
        preferences[SelectedThemeColorKey] = 123
        applyMyMusicDesign(preferences)
        assertEquals(DarkMode.OFF.name, preferences[DarkModeKey])
        assertEquals(123, preferences[SelectedThemeColorKey])
    }
}
