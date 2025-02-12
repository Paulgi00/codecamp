package com.example.wildidle.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameValueDao {
    @Query("SELECT * FROM GameValues WHERE id = 0")
    fun getGameValues(): Flow<GameValues>

    @Upsert
    suspend fun updateGameValues(gameValues: GameValues)
}

@Dao
interface ProducerDao {
    @Query("SELECT * FROM Producer")
    fun getAllProducers(): Flow<List<Producer>>

    @Upsert
    suspend fun updateProducer(producer: Producer)
}

@Dao
interface BoostDao {
    @Query("SELECT * FROM Boost")
    fun getAllBoosts(): Flow<List<Boost>>

    @Upsert
    suspend fun updateBoost(boost: Boost)
}