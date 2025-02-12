package com.example.wildidle.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wildidle.R
import com.example.wildidle.model.LeaderboardResult
import com.example.wildidle.room.GameValues
import com.example.wildidle.viewmodel.InGameViewModel
import com.example.wildidle.viewmodel.LeaderboardViewModel
import java.math.BigDecimal

@Composable
fun LeaderboardComposable() {
    val leaderboardViewModel = hiltViewModel<LeaderboardViewModel>()

    val leaderBoardEntries by leaderboardViewModel.leaderBoardEntries.collectAsState(emptyList())

    LaunchedEffect(Unit) {
        leaderboardViewModel.refreshList()
    }

    LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
        items(leaderBoardEntries) { leaderBoardEntry ->
            LeaderboardItem(leaderBoardEntry)
        }
    }
}

@Composable
fun LeaderboardItem(leaderboardResult: LeaderboardResult) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = leaderboardResult.username,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(modifier = Modifier.weight(1f))
        Text(leaderboardResult.score.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoardTopBar(topAppBarScrollBehavior: TopAppBarScrollBehavior) {

    val inGameViewModel = hiltViewModel<InGameViewModel>()
    val leaderboardViewModel = hiltViewModel<LeaderboardViewModel>()
    val gameValues by inGameViewModel.gameValues.collectAsState(
        initial = GameValues(
            0,
            BigDecimal(0),
            BigDecimal(0),
            ""
        )
    )
    TopAppBar(
        title = {
            Text("${stringResource(R.string.score)}: ${gameValues!!.score}")
        },
        actions = {
            IconButton(
                onClick = { leaderboardViewModel.refreshList() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "refresh"
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}