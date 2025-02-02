package com.example.wildidle.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wildidle.ui.theme.WildIdleTheme
import com.example.wildidle.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WildIdleTheme {
                val mainNavController = rememberNavController()

                val authViewModel = hiltViewModel<AuthViewModel>()
                val refreshToken = getSharedPreferences("prefs", MODE_PRIVATE)
                    .getString("refresh_token", "")

                val start = if (!refreshToken.isNullOrEmpty()) {
                    runBlocking {
                        val login = authViewModel.login()
                        if (login.isSuccessful) {
                            MainScreen
                        } else {
                            LoginScreen
                        }
                    }
                } else {
                    LoginScreen
                }

                NavHost(
                    navController = mainNavController,
                    startDestination = start

                    ) {
                    composable<LoginScreen>(
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                tween(500)
                            )
                        }
                    ) {
                        LoginComposable(mainNavController)


                    }
                    composable<SignUpScreen>(
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                tween(500)
                            )
                        }
                    ) {
                        SignUpComposable(mainNavController)
                    }
                    composable<MainScreen> {
                        MainScreen(mainNavController)
                    }
                }
            }
        }
    }
}

@Serializable
object LoginScreen

@Serializable
object SignUpScreen

@Serializable
object MainScreen


