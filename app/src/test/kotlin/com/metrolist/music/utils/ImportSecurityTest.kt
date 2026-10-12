package com.metrolist.music.utils

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ImportSecurityTest {
    @Test fun acceptsOnlyTrustedHttpsLoginOrigins() {
        assertTrue(LoginUrlPolicy.allows("https://accounts.google.com/ServiceLogin"))
        assertTrue(LoginUrlPolicy.allows("https://music.youtube.com:443/"))
        for (url in listOf("http://accounts.google.com/", "https://accounts.google.com.evil.test/",
            "https://accounts.google.com@evil.test/", "https://evil.test@accounts.google.com/",
            "https://accounts.google.com:8443/", "file:///etc/passwd", "intent://open", "javascript:alert(1)", "not a URL")) {
            assertFalse(url, LoginUrlPolicy.allows(url))
        }
    }
    @Test fun preservesSmallImportAndAllowsDiscardingLegacySecrets() {
        val output = ByteArrayOutputStream()
        assertEquals(3L, BoundedImport.copy(ByteArrayInputStream(byteArrayOf(1, 2, 3)), output, 3))
        assertArrayEquals(byteArrayOf(1, 2, 3), output.toByteArray())
        assertEquals(3L, BoundedImport.copy(ByteArrayInputStream(byteArrayOf(1, 2, 3)), null, 3))
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsOversizedArchiveEntry() {
        BoundedImport.copy(ByteArrayInputStream(ByteArray(5000)), ByteArrayOutputStream(), 4096)
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsDeepJsonBeforeParsing() {
        SpotifyLibraryExportParser.parse("""{"tracks":[],"other":""" + "[".repeat(33) + "0" + "]".repeat(33) + "}")
    }
    @Test fun ignoresJsonBracketsInsideSongNames() {
        val songs = SpotifyLibraryExportParser.parse("""{"tracks":[{"track":"[[[[Song]]]]","artist":"Artist"}]}""")
        assertEquals("[[[[Song]]]]", songs.tracks.single().title)
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsLargeCsvFields() {
        SpotifyLibraryExportParser.parse("title,artist\n" + "x".repeat(10001) + ",Artist")
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsManyCsvRows() {
        SpotifyLibraryExportParser.parse("title,artist\n" + "Song,Artist\n".repeat(50001))
    }
}
