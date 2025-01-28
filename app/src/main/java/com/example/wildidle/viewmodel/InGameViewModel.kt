package com.example.wildidle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.wildidle.data.IdleApi
import com.example.wildidle.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(
    private val idleApi: IdleApi,
    private val tokenStorage: TokenStorage,
    application: Application
) : AndroidViewModel(application) {
    suspend fun getItems(): Response<List<Item>> {
        return idleApi.getItems()
    }
}