package com.metrolist.music.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.metrolist.music.ui.component.AppNavigationBar
import com.metrolist.music.ui.component.ChipsRow
import com.metrolist.music.ui.component.DiscoveryCard
import com.metrolist.music.ui.component.HomeGreeting
import com.metrolist.music.ui.component.RecommendationPanel
import com.metrolist.music.utils.RecommendationPreferences
import com.metrolist.music.utils.RecommendationStatus
import com.metrolist.music.ui.screens.Screens
import com.metrolist.music.ui.theme.MetrolistTheme
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class, qualifiers = "w393dp-h852dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MyMusicUiRenderTest {
    @Test fun rendersHomeComponentsInPortrait() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()
        val activity = controller.get()
        activity.setContent {
            MetrolistTheme(darkTheme = true) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
                        HomeGreeting(null, null, {}, {}, {})
                        ChipsRow(listOf("all" to "All", "release" to "New releases", "trending" to "Trending"), "all", {})
                        Text("Curated for you", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(24.dp))
                        DiscoveryCard("Past Lives", "sapientdream · Slushii", null, {}, {}, Modifier.padding(horizontal = 24.dp).height(184.dp))
                    }
                    AppNavigationBar(Screens.MainScreens, "home", { _, _ -> }, slimNav = true,
                        modifier = Modifier.align(Alignment.BottomCenter).height(88.dp))
                }
                }
            }
        }
        val view = activity.window.decorView
        val specWidth = View.MeasureSpec.makeMeasureSpec(393, View.MeasureSpec.EXACTLY)
        val specHeight = View.MeasureSpec.makeMeasureSpec(852, View.MeasureSpec.EXACTLY)
        repeat(3) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(250))
            view.measure(specWidth, specHeight)
            view.layout(0, 0, 393, 852)
        }
        val bitmap = Bitmap.createBitmap(393, 852, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        val outputDir = File(System.getProperty("user.dir"), "build/ui-previews").apply { mkdirs() }
        File(outputDir, "home-components.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertEquals(393, bitmap.width)
        assertEquals("The composition must paint its dark background", 0xFF09090F.toInt(), bitmap.getPixel(190, 650))
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        assertTrue("Lime controls must be visible", pixels.count { it == 0xFFC7DE5A.toInt() } > 200)
        controller.pause().stop().destroy()
    }

    @Test fun rendersExpandedRecommendationChoices() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()
        val activity = controller.get()
        activity.setContent {
            MetrolistTheme(darkTheme = true) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(Modifier.padding(top = 24.dp)) {
                        RecommendationPanel(RecommendationPreferences("pop", "Hindi", "chill"), RecommendationStatus.CACHED,
                            canPlay = true, onPlay = {}, onRefresh = {}, onChoice = { _, _ -> }, onReset = {})
                    }
                }
            }
        }
        val view = activity.window.decorView
        repeat(3) {
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(250))
            view.measure(View.MeasureSpec.makeMeasureSpec(393, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(852, View.MeasureSpec.EXACTLY))
            view.layout(0, 0, 393, 852)
        }
        val bitmap = Bitmap.createBitmap(393, 852, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        val outputDir = File(System.getProperty("user.dir"), "build/ui-previews").apply { mkdirs() }
        File(outputDir, "recommendation-controls.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val pixels = IntArray(393 * 852)
        bitmap.getPixels(pixels, 0, 393, 0, 0, 393, 852)
        assertTrue("Saved choices and playback button must be visible", pixels.count { it == 0xFFC7DE5A.toInt() } > 500)
        controller.pause().stop().destroy()
    }
}
