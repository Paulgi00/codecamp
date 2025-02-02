package com.example.wildidle.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wildidle.R
import com.example.wildidle.viewmodel.AuthViewModel


@Composable
fun SettingsComposable(mainNavController: NavController) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        RowComposable(onClick = {
            authViewModel.logout()
            mainNavController.navigate(LoginScreen)
        }) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Composable
private fun RowComposable(onClick: () -> Unit, rowContent: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                onClick()
            })
            .padding(10.dp)
    ) {
        rowContent()
    }
}
