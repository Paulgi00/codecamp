package com.example.wildidle.viewmodel

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import com.example.wildidle.room.Boost
import com.example.wildidle.room.BoostDao
import com.example.wildidle.room.GameValueDao
import com.example.wildidle.room.Producer
import com.example.wildidle.room.ProducerDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundService : Service() {

    @Inject
    lateinit var gameValueDao: GameValueDao
    @Inject
    lateinit var producerDao: ProducerDao
    @Inject
    lateinit var boostDao: BoostDao

    private var activeBoosts = mutableSetOf<String>()
    private var activeBoostTimers = mutableSetOf<Timer>()

    private var producerList = mutableStateOf(emptyList<Producer>())

    private val timer = Timer()
    private var timerTask: TimerTask? = null
    private var production: Int = 0
    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            launch {
                // watch producers and calculate produce rate per second
                producerDao.getAllProducers().collect { producers ->
                    producerList.value = producers
                    production =
                        producers.stream().mapToInt { producer ->
                            if (producer.level > 0) {
                                producer.productionRate
                            } else {
                                0
                            }
                        }.sum()
                }
            }
            launch {
                // watch boosts and activate boost if a new one has been bought
                boostDao.getAllBoosts().collect { boosts ->
                    if (boosts.isEmpty()) {
                        activeBoostTimers.forEach { it.cancel() }
                        activeBoosts.clear()
                    }
                    for (boost in boosts) {
                        if (boost.durationLeft > 0 && !activeBoosts.contains(boost.name)) {
                            startBoostTimer(boost)
                        }
                    }
                }
            }
        }

        timerTask?.cancel()

        // add production rate to score and credit once a second
        timerTask = object : TimerTask() {
            override fun run() {
                scope.launch {
                    if (production > 0) {
                        val gameValues = gameValueDao.getGameValues().firstOrNull()!!
                        gameValueDao.updateGameValues(
                            gameValues.copy(
                                score = gameValues.score.plus(BigDecimal(production)),
                                credit = gameValues.credit.plus(BigDecimal(production))
                            )
                        )
                    }
                }
            }
        }

        timer.schedule(timerTask, 0, 1000)
        return START_STICKY
    }

    private fun startBoostTimer(b: Boost) {
        var boost = b
        val boostTimer = Timer()

        val boostTimerTask = object : TimerTask() {
            override fun run() {
                activeBoostTimers.add(boostTimer)
                scope.launch {
                    if (boost.durationLeft > 0) {
                        boost = boost.copy(
                            durationLeft = boost.durationLeft - 1
                        )
                        boostDao.updateBoost(boost)
                    } else {
                        activeBoostTimers.remove(boostTimer)
                        boostTimer.cancel()
                        activeBoosts.remove(boost.name)
                        boostDao.updateBoost(
                            boost.copy(
                                isActive = false
                            )
                        )
                        for (producer in producerList.value) {
                            producerDao.updateProducer(
                                producer.copy(
                                    productionRate = (producer.productionRate / boost.boostFactor).toInt()
                                )
                            )
                        }
                    }
                }
            }
        }
        // add boost to list, so it wont be executed twice and update database entry for boost
        activeBoosts.add(boost.name)
        scope.launch {
            launch {
                boost = boost.copy(
                    durationLeft = boost.duration,
                    isActive = true
                )
                boostDao.updateBoost(boost)
            }
            // if the boost is active, multiply producer values to the boost factor
            if (!b.isActive) {
                launch {
                    producerList.value.forEach {
                        producerDao.updateProducer(
                            it.copy(
                                productionRate = (it.productionRate * b.boostFactor).toInt()
                            )
                        )
                    }
                }

            }
        }
        boostTimer.schedule(boostTimerTask, 0, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancelChildren()
        activeBoostTimers.forEach { it.cancel() }
        timer.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
