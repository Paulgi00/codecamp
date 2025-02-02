package com.example.wildidle.view

import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.wildidle.R
import com.example.wildidle.model.SignInDTO
import com.example.wildidle.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUpComposable(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        var userNameText by remember {
            mutableStateOf("")
        }
        var userFirstPasswordText by remember {
            mutableStateOf("")
        }
        var userSecondPasswordText by remember {
            mutableStateOf("")
        }

        var loading by remember { mutableStateOf(false) }

        val authViewModel = hiltViewModel<AuthViewModel>()
        val coroutineScope = rememberCoroutineScope()

        fun signUp() {
            if (!loading) {
                var clientErrorMessage = ""
                if (userFirstPasswordText != userSecondPasswordText) {
                    clientErrorMessage =
                        navController.context.getString(R.string.passwords_match_error)
                } else if (userFirstPasswordText.isEmpty() || userSecondPasswordText.isEmpty()) {
                    clientErrorMessage =
                        navController.context.getString(R.string.username_empty_error)
                } else if (userNameText.isEmpty()) {
                    clientErrorMessage =
                        navController.context.getString(R.string.username_empty_error)
                } else {
                    coroutineScope.launch {
                        loading = true
                        var serverErrorMessage = ""
                        val signUpResponse = authViewModel.signUp(
                            SignInDTO(
                                userNameText,
                                userFirstPasswordText
                            )
                        )
                        if (signUpResponse.isSuccessful) {
                            val loginResponse = authViewModel.login()
                            if (loginResponse.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    navController.navigate(MainScreen) {
                                        popUpTo<LoginScreen> {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        } else if (signUpResponse.code() == 493) {
                            serverErrorMessage =
                                navController.context.getString(R.string.username_taken_error)
                        } else {
                            serverErrorMessage =
                                navController.context.getString(R.string.create_user_error)
                        }
                        if (serverErrorMessage.isNotEmpty()) {
                            Toast.makeText(
                                navController.context,
                                serverErrorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        loading = false
                    }
                }
                if (clientErrorMessage.isNotEmpty()) {
                    Toast.makeText(navController.context, clientErrorMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 30.sp
                )

                UserNameInput(
                    userNameText = userNameText,
                    onUserNameChange = { userNameText = it }
                )

                UserPasswordInput(
                    userPasswordText = userFirstPasswordText,
                    onUserPasswordChange = { userFirstPasswordText = it },
                    { },
                    imeAction = ImeAction.Next
                )

                UserPasswordInput(
                    userPasswordText = userSecondPasswordText,
                    onUserPasswordChange = { userSecondPasswordText = it },
                    { signUp() },
                    imeAction = ImeAction.Send
                )

                Button(
                    onClick = {
                        signUp()
                    }
                ) {
                    Text(stringResource(id = R.string.sign_up))
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
                    },
                    enabled = !loading
                ) {
                    Text(stringResource(id = R.string.login))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}