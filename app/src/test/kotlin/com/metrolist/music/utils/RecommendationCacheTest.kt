package com.metrolist.music.utils

import com.metrolist.innertube.models.Artist
import com.metrolist.innertube.models.SongItem
import androidx.datastore.preferences.core.mutablePreferencesOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.LocalDate

class RecommendationCacheTest {
    @get:Rule val temporary = TemporaryFolder()
    private val today = LocalDate.of(2026, 10, 12)
    private fun song(id: String, artist: String = "Artist") = SongItem(id, "Song $id", listOf(Artist(artist, "artist-id")),
        thumbnail = "https://example.com/cover.jpg", duration = 180)
    private fun snapshot(key: String) = RecommendationSnapshot(key, today.toString(), 1000L, listOf(RecommendationTrack.from(song("abc"))))

    @Test fun preservesPlayableMetadataAcrossRestartAndSeparatesTasteSelections() {
        val dir = temporary.newFolder()
        val key = RecommendationCache.key(listOf("Hindi", "pop", "liked-a"))
        RecommendationCache(dir).write(snapshot(key))
        val saved = RecommendationCache(dir).read(key)!!
        assertEquals(song("abc"), saved.tracks.single().toSong())
        assertNull(RecommendationCache(dir).read(RecommendationCache.key(listOf("Japanese", "pop", "liked-a"))))
    }
    @Test fun expiresOnNextDayAndRejectsFutureClock() {
        val saved = snapshot(RecommendationCache.key(listOf("test")))
        assertTrue(saved.isFresh(today, 2000L))
        assertFalse(saved.isFresh(today.plusDays(1), 2000L))
        assertFalse(saved.isFresh(today, 999L))
        assertFalse(saved.isFresh(today, 37 * 60 * 60 * 1000L))
    }
    @Test fun corruptOversizedOrUnsafeCacheCannotBeUsed() {
        val dir = temporary.newFolder()
        val key = RecommendationCache.key(listOf("test"))
        val cache = RecommendationCache(dir)
        File(dir, "$key.json").writeText("{broken")
        assertNull(cache.read(key))
        File(dir, "$key.json").writeBytes(ByteArray(RecommendationCache.MAX_BYTES + 1))
        assertNull(cache.read(key))
        cache.write(snapshot(key).copy(tracks = listOf(RecommendationTrack.from(song("abc").copy(thumbnail = "file:///private")))))
        assertNull(cache.read(key))
        assertNull(cache.read("../secret"))
    }
    @Test fun evictsOlderSelectionsAndDoesNotLeaveTemporaryFiles() {
        val dir = temporary.newFolder()
        val cache = RecommendationCache(dir)
        repeat(14) { index ->
            val key = RecommendationCache.key(listOf(index.toString()))
            cache.write(snapshot(key))
            File(dir, "$key.json").setLastModified(1000L + index)
        }
        assertEquals(12, dir.listFiles()!!.size)
        assertNull(cache.read(RecommendationCache.key(listOf("0"))))
        assertTrue(dir.listFiles()!!.all { it.extension == "json" })
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsTooManyTracks() {
        RecommendationCache(temporary.newFolder()).write(snapshot(RecommendationCache.key(listOf("test"))).copy(tracks = List(49) { RecommendationTrack.from(song("a$it")) }))
    }
    @Test fun ranksByTasteExcludesKnownTracksAndLimitsArtistRepetition() {
        val candidates = listOf(song("known", "Favorite"), song("new", "Other")) + List(8) { song("favorite$it", "Favorite") }
        val ranked = rankRecommendations(candidates + candidates, setOf("favorite"), setOf("known"), today)
        assertEquals("Favorite", ranked.first().artists.first().name)
        assertFalse(ranked.any { it.id == "known" })
        assertEquals(3, ranked.count { it.artists.first().name == "Favorite" })
        assertEquals(ranked.size, ranked.map { it.id }.toSet().size)
        assertEquals(ranked, rankRecommendations(candidates, setOf("favorite"), setOf("known"), today))
    }
    @Test fun validatesSavedPreferencesAndBuildsCombinedDiscoveryQuery() {
        val prefs = mutablePreferencesOf(RecommendationGenreKey to "pop", RecommendationLanguageKey to "Hindi", RecommendationStyleKey to "chill")
        assertEquals("Hindi pop chill music", RecommendationPreferences.read(prefs).searchQuery())
        prefs[RecommendationLanguageKey] = "arbitrary query"
        assertEquals("", RecommendationPreferences.read(prefs).language)
        assertFalse(RecommendationPreferences().isFiltered)
    }
}
