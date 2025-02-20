package com.example.wildidle.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wildidle.R
import com.example.wildidle.room.Boost
import com.example.wildidle.room.GameValues
import com.example.wildidle.room.Producer
import com.example.wildidle.viewmodel.InGameViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun GameComposable() {
    val inGameViewModel = hiltViewModel<InGameViewModel>()

    val gameValues by inGameViewModel.gameValues.collectAsState(
        initial = GameValues(
            0,
            BigDecimal(0),
            BigDecimal(0),
            ""
        )
    )

    val producers by inGameViewModel.producers.collectAsState(emptyList())
    val boosts by inGameViewModel.boosts.collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(R.drawable.generator_button),
            "",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .clickable { inGameViewModel.increaseScore(1) }
        )

        Column {
            for (producer in producers) {
                ProducerComposable(producer, gameValues!!)
            }
        }
        Box(modifier = Modifier.weight(1f))
        Column {
            for (boost in boosts) {
                if (boost.isActive) {
                    BoostComposable(boost)
                }
            }
        }
    }
}

@Composable
fun ProducerComposable(producer: Producer, gameValues: GameValues) {
    val inGameViewModel = hiltViewModel<InGameViewModel>()
    val coroutine = rememberCoroutineScope()

    val notLoading = !inGameViewModel.isLoading.value && producer.level > 0 && producer.level < 5
    val levelZero = producer.level == 0 && gameValues.credit >= BigDecimal(producer.cost)
    val enoughCredit =
        notLoading && BigDecimal(inGameViewModel.getUpgrade(producer)!!.cost) <= gameValues.credit

    val buyAble = levelZero || enoughCredit

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(producer.imageId),
            modifier = Modifier
                .size(112.dp)
                .padding(end = 10.dp),
            contentDescription = "producer icon",
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(producer.displayName))
            if (producer.level > 0) Text("Level ${producer.level}")
        }
        Box(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.padding(end = 10.dp)) {
            if (producer.level > 0) {
                Text("${producer.productionRate}")
                BtcIcon()
                Text("/s")
            }
        }
        Button(
            enabled = buyAble,
            onClick = {
                coroutine.launch {
                    inGameViewModel.buyUpgrade(producer)
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (producer.level <= 0) {
                    Text(stringResource(R.string.buy))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(producer.cost.toString())
                        BtcIcon()
                    }

                } else if (producer.level > 4) {
                    Text(stringResource(R.string.max_level))
                } else {
                    Text(stringResource(R.string.upgrade))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (inGameViewModel.isLoading.value) {
                            Text("Loading...")
                        } else {
                            Text(inGameViewModel.getUpgrade(producer)!!.cost.toString())
                        }
                        BtcIcon()
                    }
                }


            }
        }


    }
}

@Composable
fun BoostComposable(boost: Boost) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(boost.imageId),
                contentDescription = "Boost Icon",
                modifier = Modifier
                    .size(112.dp)
                    .padding(end = 10.dp),
            )
            Text(stringResource(boost.displayName))
            Column(
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(stringResource(boost.displayName))
                Text("Factor: ${boost.boostFactor}x")
            }
        }

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { 1f - (boost.durationLeft / boost.duration.toFloat()) }
        )
    }
}

@Composable
fun BtcIcon() {
    Icon(
        imageVector = Icons.Filled.CurrencyBitcoin,
        contentDescription = "Coins"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar() {
    val inGameViewModel = hiltViewModel<InGameViewModel>()
    val gameValues by inGameViewModel.gameValues.collectAsState(
        initial = GameValues(
            0,
            BigDecimal(0),
            BigDecimal(0),
            ""
        )
    )

    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            gameValues?.let {
                Text(
                    text = it.name,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            Text(
                gameValues?.credit.toString(),
                fontSize = 20.sp
            )
            BtcIcon()
        }
    )
}