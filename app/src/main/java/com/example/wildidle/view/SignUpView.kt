package com.example.wildidle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SignUpComposable(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var userNameText by remember {
                mutableStateOf("")
            }

            var userFirstPasswordText by remember {
                mutableStateOf("")
            }

            var userSecondPasswordText by remember {
                mutableStateOf("")
            }

            Box(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "WildIdle",
                    fontSize = 30.sp
                )

                UserNameInput(
                    userNameText = userNameText,
                    onUserNameChange = { userNameText = it }
                )

                UserPasswordInput(
                    userPasswordText = userFirstPasswordText,
                    onUserPasswordChange = { userFirstPasswordText = it }
                )

                UserPasswordInput(
                    userPasswordText = userSecondPasswordText,
                    onUserPasswordChange = { userSecondPasswordText = it }
                )

                Button(
                    onClick = { }
                ) {
                    Text("Sign Up")
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.popBackStack(LoginScreen, false)
                    }
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}