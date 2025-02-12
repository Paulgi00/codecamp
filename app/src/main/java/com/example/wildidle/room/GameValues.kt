package com.example.wildidle.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.math.BigDecimal

@Entity
@TypeConverters(BigDecimalConverter::class)
data class GameValues(
    @PrimaryKey val id: Int,
    val credit: BigDecimal,
    val score: BigDecimal,
    val name: String
)
