/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.metrolist.music.LocalNavController
import com.metrolist.music.R
import com.metrolist.music.constants.AlbumViewTypeKey
import com.metrolist.music.constants.ChipSortTypeKey
import com.metrolist.music.constants.LibraryFilter
import com.metrolist.music.constants.LibraryViewType
import com.metrolist.music.constants.PlaylistViewTypeKey
import com.metrolist.music.ui.component.ChipsRow
import com.metrolist.music.utils.rememberEnumPreference

@Composable
fun LibraryScreen() {
    val navController = LocalNavController.current
    var filterType by rememberEnumPreference(ChipSortTypeKey, LibraryFilter.LIBRARY)
    var libraryViewType by rememberEnumPreference(AlbumViewTypeKey, LibraryViewType.LIST)
    var playlistViewType by rememberEnumPreference(PlaylistViewTypeKey, LibraryViewType.LIST)

    val filterContent = @Composable {
        Row {
            ChipsRow(
                chips = listOf(
                    LibraryFilter.LIBRARY.name to stringResource(R.string.design_filter_all),
                    LibraryFilter.PLAYLISTS.name to stringResource(R.string.filter_playlists),
                    "liked" to stringResource(R.string.design_liked_songs),
                    "downloaded" to stringResource(R.string.design_downloads),
                    LibraryFilter.SONGS.name to stringResource(R.string.filter_songs),
                    LibraryFilter.ALBUMS.name to stringResource(R.string.filter_albums),
                    LibraryFilter.ARTISTS.name to stringResource(R.string.filter_artists),
                    LibraryFilter.PODCASTS.name to stringResource(R.string.filter_podcasts),
                ),
                currentValue = filterType.name,
                onValueUpdate = {
                    if (it == "liked" || it == "downloaded") navController.navigate("auto_playlist/$it")
                    else {
                        val next = LibraryFilter.valueOf(it)
                        filterType = if (filterType == next) LibraryFilter.LIBRARY else next
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (filterType) {
            LibraryFilter.LIBRARY -> LibraryMixScreen(
                navController = navController,
                filterContent = filterContent,
                viewType = libraryViewType,
                onViewTypeChange = { libraryViewType = it },
            )
            LibraryFilter.PLAYLISTS -> LibraryPlaylistsScreen(
                navController = navController,
                filterContent = filterContent,
                viewType = playlistViewType,
                onViewTypeChange = { playlistViewType = it },
            )
            LibraryFilter.SONGS -> LibrarySongsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY },
            )
            LibraryFilter.ALBUMS -> LibraryAlbumsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY },
            )
            LibraryFilter.ARTISTS -> LibraryArtistsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY },
            )
            LibraryFilter.PODCASTS -> LibraryPodcastsScreen(
                navController,
                { filterType = LibraryFilter.LIBRARY },
            )
        }
    }
}
