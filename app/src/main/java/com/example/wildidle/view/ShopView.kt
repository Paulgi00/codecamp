package com.example.wildidle.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wildidle.R
import com.example.wildidle.room.Boost
import com.example.wildidle.room.GameValues
import com.example.wildidle.viewmodel.InGameViewModel
import java.math.BigDecimal

@Composable
fun ShopComposable() {
    val inGameViewModel = hiltViewModel<InGameViewModel>()

    val shopItems by inGameViewModel.boosts.collectAsState()
    val gameValues by inGameViewModel.gameValues.collectAsState()



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        for (shopItem in shopItems) {
            ShopItem(shopItem, gameValues!!, inGameViewModel)
        }
    }
}

@Composable
fun ShopItem(boost: Boost, gameValues: GameValues, inGameViewModel: InGameViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(boost.imageId),
            modifier = Modifier.size(112.dp),
            contentDescription = ""
        )
        Text(stringResource(boost.displayName))
        Column {
            Text("Factor: ${boost.boostFactor}x")
            Text("Duration: ${boost.duration}s")
        }
        Button(
            enabled = !boost.isActive && gameValues.credit >= BigDecimal(boost.cost),
            onClick = {
                inGameViewModel.buyBoost(boost)
            }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Buy")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(boost.cost.toString())
                    BtcIcon()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopTopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.shop)) }
    )
}