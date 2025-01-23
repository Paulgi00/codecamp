package com.example.wildidle.view

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoginComposable(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f))
            InnerColumn()
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


private fun login() {
    // Todo login
}

@Composable
fun InnerColumn() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var userNameText by remember {
            mutableStateOf("")
        }

        var userPasswordText by remember {
            mutableStateOf("")
        }
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
            onUserPasswordChange = { userPasswordText = it }
        )

        Button(
            onClick = { }
        ) {
            Text("Login")
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
fun UserPasswordInput(userPasswordText: String, onUserPasswordChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier,
        value = userPasswordText,
        onValueChange = onUserPasswordChange,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(
            onAny = {
                login()
            }
        ),
        label = {
            Text("Password")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Password,
                contentDescription = "User"
            )
        }
    )
}
