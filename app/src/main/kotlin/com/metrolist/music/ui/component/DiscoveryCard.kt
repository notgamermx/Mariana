package com.metrolist.music.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.metrolist.music.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoveryCard(title: String, artists: String, artwork: String?, onPlay: () -> Unit, onMore: (() -> Unit)?, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxSize().combinedClickable(onClick = onPlay, onLongClick = onMore),
        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFD1A3EB))) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth < 180.dp) {
                AsyncImage(model = artwork, contentDescription = title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(0.6f).fillMaxHeight().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                            color = Color(0xFF211029), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(artists, style = MaterialTheme.typography.bodySmall, color = Color(0xFF432D4D), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(onClick = onPlay, shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF391447), contentColor = Color.White),
                                modifier = Modifier.size(48.dp)) {
                                Icon(painterResource(R.drawable.play), contentDescription = stringResource(R.string.play))
                            }
                            if (onMore != null) IconButton(onClick = onMore) {
                                Icon(painterResource(R.drawable.more_vert), contentDescription = stringResource(R.string.design_more_options), tint = Color(0xFF211029))
                            }
                        }
                    }
                    AsyncImage(model = artwork, contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.weight(0.4f).fillMaxHeight())
                }
            }
        }
    }
}
