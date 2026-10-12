package com.metrolist.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.metrolist.music.R

@Composable
fun HomeGreeting(accountName: String?, accountImageUrl: String?, onAccount: () -> Unit, onSearch: () -> Unit, onLikes: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.radialGradient(listOf(Color(0xFF41203F).copy(alpha = 0.6f), Color.Transparent), radius = 440f))
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(onClick = onAccount, modifier = Modifier.size(48.dp), shape = CircleShape) {
                if (accountImageUrl != null) AsyncImage(model = accountImageUrl, contentDescription = stringResource(R.string.account),
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                else Icon(painterResource(R.drawable.account), contentDescription = stringResource(R.string.account))
            }
            Spacer(Modifier.weight(1f))
            FilledTonalIconButton(onClick = onSearch, shape = CircleShape) {
                Icon(painterResource(R.drawable.search), contentDescription = stringResource(R.string.search))
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalIconButton(onClick = onLikes, shape = CircleShape) {
                Icon(painterResource(R.drawable.favorite_border), contentDescription = stringResource(R.string.design_liked_songs))
            }
        }
        val name = accountName?.trim()?.takeIf { it.isNotEmpty() }?.substringBefore(' ')
        Text(text = if (name != null) stringResource(R.string.design_greeting_name, name) else stringResource(R.string.design_greeting),
            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
