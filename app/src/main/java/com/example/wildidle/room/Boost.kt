package com.example.wildidle.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Boost(
    @PrimaryKey val name: String,
    val cost: Int,
    val duration: Int,
    val boostFactor: Double,
    val durationLeft: Int = 0,
    val displayName: Int,
    val imageId: Int,
    val isActive: Boolean = false
)
