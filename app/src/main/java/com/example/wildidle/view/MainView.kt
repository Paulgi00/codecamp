package com.example.wildidle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wildidle.viewmodel.AuthViewModel

@Composable
fun MainScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val authViewModel = hiltViewModel<AuthViewModel>()


            Button(modifier = Modifier,
                onClick = {
                    authViewModel.logout()
                    navController.navigate(LoginScreen) {
                        popUpTo<MainScreen> {
                            inclusive = true
                        }
                    }
                }) { Text("Logout") }
        }
    }
}