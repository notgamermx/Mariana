package com.metrolist.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import com.metrolist.music.R
import com.metrolist.music.utils.*

@Composable
fun RecommendationPanel(
    preferences: RecommendationPreferences, status: RecommendationStatus, canPlay: Boolean,
    onPlay: () -> Unit, onRefresh: () -> Unit, onChoice: (Preferences.Key<String>, String) -> Unit, onReset: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(preferences.isFiltered) }
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.rec_daily), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.rec_basis), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val statusLabel = when (status) {
                RecommendationStatus.LOADING -> R.string.rec_loading
                RecommendationStatus.SAVED -> R.string.rec_saved
                RecommendationStatus.LIVE -> R.string.rec_live
                RecommendationStatus.CACHED -> R.string.rec_cached
                RecommendationStatus.OFFLINE -> R.string.rec_offline
                RecommendationStatus.LIBRARY -> R.string.rec_library
                RecommendationStatus.EMPTY -> R.string.rec_empty
                RecommendationStatus.FAILED -> R.string.rec_failed
            }
            Text(stringResource(statusLabel), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (status == RecommendationStatus.LOADING) LinearProgressIndicator(Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPlay, enabled = canPlay) { Text(stringResource(R.string.rec_play)) }
                OutlinedButton(onClick = onRefresh, enabled = status != RecommendationStatus.LOADING) { Text(stringResource(R.string.rec_refresh)) }
            }
            if (preferences.isFiltered) {
                val selected = listOf(
                    RecommendationChoices.genres.first { it.first == preferences.genre },
                    RecommendationChoices.languages.first { it.first == preferences.language },
                    RecommendationChoices.styles.first { it.first == preferences.style })
                    .filter { it.first.isNotEmpty() }.map { stringResource(it.second) }.joinToString(" · ")
                Text(selected, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.rec_tune))
            }
            AnimatedVisibility(expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.rec_genre), style = MaterialTheme.typography.labelLarge)
                    ChipsRow(RecommendationChoices.genres.map { it.first to stringResource(it.second) }, preferences.genre,
                        { onChoice(RecommendationGenreKey, it) })
                    Text(stringResource(R.string.rec_language), style = MaterialTheme.typography.labelLarge)
                    ChipsRow(RecommendationChoices.languages.map { it.first to stringResource(it.second) }, preferences.language,
                        { onChoice(RecommendationLanguageKey, it) })
                    Text(stringResource(R.string.rec_style), style = MaterialTheme.typography.labelLarge)
                    ChipsRow(RecommendationChoices.styles.map { it.first to stringResource(it.second) }, preferences.style,
                        { onChoice(RecommendationStyleKey, it) })
                    Text(stringResource(R.string.rec_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onReset) { Text(stringResource(R.string.rec_reset)) }
                }
            }
        }
    }
}
