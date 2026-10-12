package com.metrolist.music.utils

import java.net.URI
import java.util.Locale

object LoginUrlPolicy {
    private val hosts = setOf("accounts.google.com", "music.youtube.com", "www.youtube.com", "consent.google.com", "myaccount.google.com")

    fun allows(url: String): Boolean = try {
        val uri = URI(url)
        uri.scheme.equals("https", ignoreCase = true) && uri.rawUserInfo == null &&
            uri.host?.lowercase(Locale.ROOT) in hosts && (uri.port == -1 || uri.port == 443)
    } catch (_: Exception) { false }
}
