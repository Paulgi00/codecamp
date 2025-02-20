package com.example.wildidle.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildidle.R
import com.example.wildidle.api.IdleApi
import com.example.wildidle.model.Item
import com.example.wildidle.model.Upgrade
import com.example.wildidle.room.Boost
import com.example.wildidle.room.BoostDao
import com.example.wildidle.room.GameValueDao
import com.example.wildidle.room.GameValues
import com.example.wildidle.room.Producer
import com.example.wildidle.room.ProducerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InGameViewModel @Inject constructor(
    private val gameValueDao: GameValueDao,
    private val producerDao: ProducerDao,
    private val boostDao: BoostDao,
    private val idleApi: IdleApi
) : ViewModel() {
    private var serverItemList: List<Item>? = null

    private var upgradeMap: MutableMap<Int, Upgrade> = mutableMapOf()

    private val imageMap = mapOf(
        "low passive" to R.drawable.laptop_image,
        "medium passive" to R.drawable.graphics_card_image,
        "high passive" to R.drawable.quant_comp_image,
        "low Boost" to R.drawable.referral_scam_image,
        "medium Boost" to R.drawable.ai_trading_bot_image,
        "high Boost" to R.drawable.rugpull_scheme_image
    )

    private val nameMap = mapOf(
        "low passive" to R.string.laptop,
        "medium passive" to R.string.gpu,
        "high passive" to R.string.quantComp,
        "low Boost" to R.string.referral_scam,
        "medium Boost" to R.string.ai_trading_bot,
        "high Boost" to R.string.rugpull_scheme

    )

    private val _gameValues = MutableStateFlow<GameValues?>(null)
    val gameValues: StateFlow<GameValues?> = _gameValues

    private val _producers = MutableStateFlow<List<Producer>>(emptyList())
    val producers: StateFlow<List<Producer>> = _producers

    private val _boosts = MutableStateFlow<List<Boost>>(emptyList())
    val boosts = _boosts

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            launch {
                val gameValueFlow = gameValueDao.getGameValues()
                if (gameValueFlow.firstOrNull() == null) {
                    val newGameValues = GameValues(
                        id = 0,
                        credit = BigDecimal(0),
                        score = BigDecimal(0),
                        name = "Default"
                    )
                    gameValueDao.updateGameValues(newGameValues)
                }
                gameValueFlow.collect { values ->
                    _gameValues.value = values
                }
            }
            launch {
                val producerFlow = producerDao.getAllProducers()
                if (serverItemList == null) {
                    serverItemList = idleApi.getItems().body()!!
                }
                if (producerFlow.firstOrNull().isNullOrEmpty()) {
                    val serverProducers = serverItemList!!.stream().filter { it.unitSec != null }
                    for (producer in serverProducers) {
                        producerDao.updateProducer(
                            Producer(
                                name = producer.name,
                                cost = producer.cost,
                                productionRate = producer.unitSec!!,
                                imageId = imageMap[producer.name]
                                    ?: R.drawable.ic_launcher_foreground,
                                displayName = nameMap[producer.name] ?: R.string.def
                            )
                        )
                    }
                }
                initializeUpgrades()
                initializeShopItems()
                _isLoading.value = false
                launch {
                    producerFlow.collect { values ->
                        _producers.value = values
                    }
                }
                launch {
                    boostDao.getAllBoosts().collect {
                        _boosts.value = it
                    }
                }
            }
        }
    }

    private suspend fun initializeShopItems() {
        val boosts = (serverItemList ?: emptyList()).stream().filter { it.duration != null }
        for (boost in boosts) {
            boostDao.updateBoost(
                Boost(
                    name = boost.name,
                    cost = boost.cost,
                    duration = boost.duration ?: 0,
                    boostFactor = boost.boostFactor ?: 1.0,
                    displayName = nameMap[boost.name] ?: R.string.def,
                    imageId = imageMap[boost.name] ?: R.drawable.ic_launcher_foreground
                )
            )
        }
    }

    private fun initializeUpgrades() {
        // create name and level list to map custom name and picture and level to upgrades
        val nameList = listOf("upgrade lvl 2", "upgrade lvl 3", "upgrade lvl 4", "upgrade lvl 5")
        val levelList = (1..4)
        for (level in levelList) {
            val item =
                serverItemList!!.stream().filter { it.name == nameList[level - 1] }.findFirst()
                    .get()
            upgradeMap[level] = Upgrade(
                name = item.name,
                cost = item.cost,
                multiplier = item.multiplier ?: 1f
            )
        }
    }

    fun increaseScore(value: Int) {
        // increase the score and the credit
        viewModelScope.launch {
            _gameValues.value?.let { currentGameValues ->
                val updatedGameValues =
                    currentGameValues.copy(
                        credit = currentGameValues.credit + BigDecimal(value),
                        score = currentGameValues.score + BigDecimal(value)
                    )
                gameValueDao.updateGameValues(updatedGameValues)
            }
        }
    }

    suspend fun buyUpgrade(producer: Producer) {
        // if producer is not bought yet (level 0) buy producer
        if (producer.level <= 0) {
            gameValueDao.updateGameValues(
                gameValues.value!!.copy(
                    credit = gameValues.value!!.credit.minus(BigDecimal(producer.cost))
                )
            )

            producerDao.updateProducer(
                producer.copy(
                    level = 1
                )
            )
            // if producer is already bought, get the upgrade for the producer and apply it
        } else if (producer.level <= 4) {
            val upgrade = upgradeMap[producer.level]
            gameValueDao.updateGameValues(
                gameValues.value!!.copy(
                    credit = gameValues.value!!.credit.minus(BigDecimal(upgrade!!.cost))
                )
            )
            producerDao.updateProducer(
                producer.copy(
                    productionRate = (producer.productionRate * upgrade.multiplier).toInt(),
                    level = producer.level + 1
                )
            )
        }
    }

    fun getUpgrade(producer: Producer): Upgrade? {
        return if (producer.level in 1..4) {
            upgradeMap[producer.level]
        } else {
            null
        }
    }

    fun buyBoost(boost: Boost) {
        // update the database entry, everything else is handled by the background service
        if (boost.durationLeft == 0) {
            viewModelScope.launch {
                gameValues.value?.let {
                    gameValueDao.updateGameValues(
                        it.copy(
                            credit = gameValues.value!!.credit - BigDecimal(boost.duration)
                        )
                    )
                }
                boostDao.updateBoost(
                    boost.copy(
                        durationLeft = boost.duration
                    )
                )
            }
        }
    }
}