package com.metrolist.music.utils

import com.metrolist.innertube.models.Artist
import com.metrolist.innertube.models.SongItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.LocalDate

@Serializable
data class RecommendationTrack(
    val id: String, val title: String, val artistNames: List<String>, val artistIds: List<String?>,
    val thumbnail: String, val duration: Int?, val explicit: Boolean, val musicVideoType: String?,
) {
    fun toSong() = SongItem(id = id, title = title,
        artists = artistNames.mapIndexed { index, name -> Artist(name, artistIds.getOrNull(index)) },
        thumbnail = thumbnail, duration = duration, explicit = explicit, musicVideoType = musicVideoType)
    companion object {
        fun from(song: SongItem) = RecommendationTrack(song.id, song.title, song.artists.map { it.name },
            song.artists.map { it.id }, song.thumbnail, song.duration, song.explicit, song.musicVideoType)
    }
}

@Serializable
data class RecommendationSnapshot(val key: String, val day: String, val savedAt: Long, val tracks: List<RecommendationTrack>) {
    fun isFresh(today: LocalDate, now: Long) = day == today.toString() && now >= savedAt && now - savedAt < 36 * 60 * 60 * 1000L
}

class RecommendationCache(private val directory: File) {
    private val json = Json { ignoreUnknownKeys = true }
    @Synchronized fun read(key: String): RecommendationSnapshot? = runCatching {
        val file = File(directory, "$key.json")
        if (!validKey(key) || !file.isFile || file.length() > MAX_BYTES) return null
        json.decodeFromString<RecommendationSnapshot>(file.readText()).takeIf { snapshot ->
            snapshot.key == key && snapshot.tracks.size in 1..48 && snapshot.tracks.all { track ->
                track.id.matches(Regex("[A-Za-z0-9_-]{1,128}")) && track.title.length in 1..1000 &&
                    track.artistNames.size <= 20 && track.artistNames.all { it.length <= 1000 } &&
                    track.artistIds.size <= 20 && track.artistIds.all { it == null || it.length <= 128 } &&
                    track.thumbnail.length <= 4096 && (track.thumbnail.isEmpty() || track.thumbnail.startsWith("https://"))
            }
        }
    }.getOrNull()

    @Synchronized fun write(snapshot: RecommendationSnapshot) {
        require(validKey(snapshot.key) && snapshot.tracks.size in 1..48)
        directory.mkdirs()
        val bytes = json.encodeToString(snapshot).toByteArray(Charsets.UTF_8)
        require(bytes.size <= MAX_BYTES)
        val target = File(directory, "${snapshot.key}.json")
        val temporary = File(directory, "${snapshot.key}.tmp")
        try {
            temporary.writeBytes(bytes)
            Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } finally { temporary.delete() }
        directory.listFiles()?.filter { it.extension == "json" }?.sortedByDescending { it.lastModified() }
            ?.drop(12)?.forEach { it.delete() }
    }

    companion object {
        const val MAX_BYTES = 256 * 1024
        fun key(parts: List<String>) = MessageDigest.getInstance("SHA-256")
            .digest(parts.joinToString("\u0000").toByteArray()).joinToString("") { "%02x".format(it) }
        private fun validKey(key: String) = key.matches(Regex("[a-f0-9]{64}"))
    }
}

internal fun rankRecommendations(candidates: List<SongItem>, favoriteArtists: Set<String>, knownIds: Set<String>, day: LocalDate): List<SongItem> {
    val sorted = candidates.asSequence().filterNot { it.id in knownIds || it.isEpisode }
        .distinctBy { it.id }.sortedWith(compareByDescending<SongItem> { song ->
            song.artists.count { it.name.lowercase() in favoriteArtists }
        }.thenBy { (it.id + day.toString()).hashCode() }).toList()
    val artistCounts = mutableMapOf<String, Int>()
    return sorted.filter { song ->
        val artist = song.artists.firstOrNull()?.name?.lowercase() ?: song.id
        val count = artistCounts.getOrDefault(artist, 0)
        if (count >= 3) false else { artistCounts[artist] = count + 1; true }
    }.take(48)
}
