package com.metrolist.music.utils

import java.io.InputStream
import java.io.OutputStream

object BoundedImport {
    fun copy(input: InputStream, output: OutputStream?, maxBytes: Long): Long {
        val buffer = ByteArray(8192)
        var total = 0L
        while (true) {
            val count = input.read(buffer)
            if (count < 0) return total
            total += count
            require(total <= maxBytes) { "Import exceeds size limit" }
            output?.write(buffer, 0, count)
        }
    }
}
