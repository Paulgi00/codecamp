package com.example.wildidle.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wildidle.model.SignInDTO
import com.example.wildidle.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun LoginComposable(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    )
    { innerPadding ->
        var userNameText by remember {
            mutableStateOf("")
        }

        var userPasswordText by remember {
            mutableStateOf("")
        }

        var loading by remember { mutableStateOf(false) }

        val authViewModel = hiltViewModel<AuthViewModel>()
        val coroutineScope = rememberCoroutineScope()

        fun login() {
            if (!loading) {
                var clientErrorMessage = ""
                if (userNameText.isEmpty()) {
                    clientErrorMessage = "username cannot be empty"
                } else if (userPasswordText.isEmpty()) {
                    clientErrorMessage = "password cannot be empty"
                } else {
                    loading = true
                    coroutineScope.launch {
                        var serverErrorMessage = ""
                        val signInResponse = authViewModel
                            .signIn(SignInDTO(userNameText, userPasswordText))
                        if (signInResponse.isSuccessful) {
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
                        } else if (signInResponse.code() == 489) {
                            serverErrorMessage = "username and password don't match"
                        } else {
                            serverErrorMessage = "error while logging in"
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
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    userNameText,
                    onUserNameChange = { userNameText = it }
                )
                UserPasswordInput(
                    userPasswordText = userPasswordText,
                    onUserPasswordChange = { userPasswordText = it },
                    action = { login() },
                    imeAction = ImeAction.Send

                )
                Button(
                    onClick = { login() },
                    enabled = !loading
                ) {
                    Text("Login")
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(SignUpScreen) }
                ) {
                    Text("Sign Up")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun UserNameInput(userNameText: String, onUserNameChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier,
        value = userNameText,
        onValueChange = onUserNameChange,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Next
        ),
        label = {
            Text("Username")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "User"
            )
        }
    )
}

@Composable
fun UserPasswordInput(
    userPasswordText: String, onUserPasswordChange: (String) -> Unit,
    action: () -> Unit,
    imeAction: ImeAction
) {
    OutlinedTextField(
        modifier = Modifier,
        value = userPasswordText,
        onValueChange = onUserPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                action()
            }
        ),
        label = {
            Text("Password")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Password,
                contentDescription = "Password"
            )
        }
    )
}
