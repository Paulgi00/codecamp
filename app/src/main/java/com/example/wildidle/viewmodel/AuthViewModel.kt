package com.example.wildidle.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildidle.data.IdleApi
import com.example.wildidle.model.SignInDTO
import com.example.wildidle.model.StringResponse
import com.example.wildidle.room.GameValueDao
import com.example.wildidle.room.GameValues
import com.example.wildidle.room.RoomDatabases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val idleApi: IdleApi,
    private val tokenStorage: TokenStorage,
    application: Application,
    private val gameValueDao: GameValueDao,
    private val databases: RoomDatabases
) : AndroidViewModel(application) {

    suspend fun signUp(signInDTO: SignInDTO): Response<StringResponse> {
        val response: Response<StringResponse> = idleApi.signUp(signInDTO)
        saveRefreshToken(response)
        return response
    }

    suspend fun signIn(signInDTO: SignInDTO): Response<StringResponse> {
        val response: Response<StringResponse> = idleApi.signIn(signInDTO)
        saveRefreshToken(response)
        return response
    }

    suspend fun login(): Response<StringResponse> {
        val response = idleApi.login()
        tokenStorage.accessToken = response.headers().values("authorization")[0]
        return response
    }

    fun logout() {
        tokenStorage.accessToken = ""
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("refresh_token", "")
            apply()
        }


    }

    private fun saveRefreshToken(response: Response<StringResponse>) {
        if (response.isSuccessful) {
            val sharedPreferences = getApplication<Application>().getSharedPreferences(
                "prefs",
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.apply {
                putString("refresh_token", response.headers().values("set-cookie")[0])
                apply()
            }
        }
    }

    fun setInitialValues(userNameText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databases.clearAllTables()
            gameValueDao.updateGameValues(
                GameValues(
                    id = 0,
                    credit = BigDecimal(0),
                    score = BigDecimal(0),
                    name = userNameText
                )
            )

        }
    }
}
