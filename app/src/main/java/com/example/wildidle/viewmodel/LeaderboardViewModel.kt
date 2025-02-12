package com.example.wildidle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildidle.data.IdleApi
import com.example.wildidle.model.LeaderboardResult
import com.example.wildidle.model.ScoreBody
import com.example.wildidle.model.StringResponse
import com.example.wildidle.room.GameValueDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val idleApi: IdleApi,
    private val gameValueDao: GameValueDao
) : ViewModel() {

    private val _leaderBoardEntries = MutableStateFlow<List<LeaderboardResult>>(emptyList())
    val leaderBoardEntries = _leaderBoardEntries

    fun refreshList() {
        viewModelScope.launch {
            postScore()
            _leaderBoardEntries.value = getLeaderboard().body()
                ?.filter { it.score.signum() > 0 && it.username.isNotBlank() }
                ?.sortedByDescending { it.score }
                ?: emptyList()
        }
    }

    private suspend fun postScore(): Response<StringResponse> {
        val gameValues = gameValueDao.getGameValues().first()
        return idleApi.postScore(
            ScoreBody(
                username = gameValues.name,
                score = gameValues.score
            )
        )
    }

    private suspend fun getLeaderboard() = idleApi.getScore()
}
