package com.example.wildidle.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wildidle.R
import com.example.wildidle.viewmodel.AuthViewModel
import com.example.wildidle.viewmodel.SettingsViewModel

@Composable
fun SettingsComposable(mainNavController: NavController) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
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

        var expanded by remember { mutableStateOf(false) }
        RowComposable(onClick = {
            expanded = !expanded
        }) {
            Box {
                Text(stringResource(R.string.select_language))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("German") },
                    onClick = {
                        expanded = false
                        settingsViewModel.setLanguage("de")
                    }
                )
                DropdownMenuItem(
                    text = { Text("English") },
                    onClick = {
                        expanded = false
                        settingsViewModel.setLanguage("en")
                    }
                )
                DropdownMenuItem(
                    text = { Text("System Default") },
                    onClick = {
                        expanded = false
                        settingsViewModel.setLanguage("sd")
                    }
                )

            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) }
    )
}