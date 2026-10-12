package com.metrolist.music.viewmodels

import android.app.Application
import android.net.Uri
import androidx.room.Room
import com.metrolist.music.db.InternalDatabase
import com.metrolist.music.db.MusicDatabase
import com.metrolist.music.db.entities.SongEntity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
class BackupPrivacyTest {
    @Test fun exportsLibraryWithoutAccountSettingsOrCookies() {
        val context = RuntimeEnvironment.getApplication()
        val database = Room.databaseBuilder(context, InternalDatabase::class.java, "backup-privacy-test.db")
            .allowMainThreadQueries().build()
        val preferences = File(context.filesDir, "datastore/${BackupRestoreViewModel.SETTINGS_FILENAME}")
        preferences.parentFile!!.mkdirs()
        preferences.writeText("private-cookie-marker")
        val backup = File(context.cacheDir, "privacy-test.backup")
        try {
            database.dao.insert(SongEntity(id = "song", title = "Saved song"))
            BackupRestoreViewModel(MusicDatabase(database)).backup(context, Uri.fromFile(backup))
            ZipFile(backup).use { zip ->
                assertNotNull(zip.getEntry(InternalDatabase.DB_NAME))
                assertNull(zip.getEntry(BackupRestoreViewModel.SETTINGS_FILENAME))
            }
            assertEquals("private-cookie-marker", preferences.readText())
        } finally {
            database.close()
            context.deleteDatabase("backup-privacy-test.db")
            backup.delete()
            preferences.delete()
        }
    }
}
