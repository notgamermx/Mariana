package com.metrolist.music.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.metrolist.music.R

val RecommendationGenreKey = stringPreferencesKey("recommendationGenre")
val RecommendationLanguageKey = stringPreferencesKey("recommendationLanguage")
val RecommendationStyleKey = stringPreferencesKey("recommendationStyle")

data class RecommendationPreferences(val genre: String = "", val language: String = "", val style: String = "") {
    val isFiltered get() = listOf(genre, language, style).any { it.isNotEmpty() }
    fun searchQuery() = listOf(language, genre, style, "music").filter { it.isNotEmpty() }.joinToString(" ")
    companion object {
        fun read(prefs: Preferences) = RecommendationPreferences(
            prefs[RecommendationGenreKey]?.takeIf { value -> RecommendationChoices.genres.any { it.first == value } } ?: "",
            prefs[RecommendationLanguageKey]?.takeIf { value -> RecommendationChoices.languages.any { it.first == value } } ?: "",
            prefs[RecommendationStyleKey]?.takeIf { value -> RecommendationChoices.styles.any { it.first == value } } ?: "")
    }
}

object RecommendationChoices {
    val genres = listOf("" to R.string.rec_any, "pop" to R.string.rec_pop, "rock" to R.string.rec_rock,
        "hip hop" to R.string.rec_hiphop, "electronic" to R.string.rec_electronic, "indie" to R.string.rec_indie,
        "R&B" to R.string.rec_rnb, "jazz" to R.string.rec_jazz, "classical" to R.string.rec_classical,
        "folk" to R.string.rec_folk, "metal" to R.string.rec_metal, "anime" to R.string.rec_anime,
        "soundtrack" to R.string.rec_soundtrack, "phonk" to R.string.rec_phonk)
    val languages = listOf("" to R.string.rec_any, "English" to R.string.rec_english, "Hindi" to R.string.rec_hindi,
        "Tamil" to R.string.rec_tamil, "Telugu" to R.string.rec_telugu, "Malayalam" to R.string.rec_malayalam,
        "Punjabi" to R.string.rec_punjabi, "Bengali" to R.string.rec_bengali, "Marathi" to R.string.rec_marathi,
        "Japanese" to R.string.rec_japanese, "Korean" to R.string.rec_korean, "Spanish" to R.string.rec_spanish,
        "French" to R.string.rec_french, "Arabic" to R.string.rec_arabic)
    val styles = listOf("" to R.string.rec_any, "chill" to R.string.rec_chill, "energetic" to R.string.rec_energetic,
        "romantic" to R.string.rec_romantic, "sad" to R.string.rec_sad, "lofi" to R.string.rec_lofi,
        "acoustic" to R.string.rec_acoustic, "instrumental" to R.string.rec_instrumental,
        "workout" to R.string.rec_workout, "focus" to R.string.rec_focus, "slowed" to R.string.rec_slowed,
        "sped up" to R.string.rec_spedup)
}

enum class RecommendationStatus { LOADING, SAVED, LIVE, CACHED, OFFLINE, LIBRARY, EMPTY, FAILED }
