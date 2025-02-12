package com.example.wildidle.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Producer(
    @PrimaryKey val name: String,
    val cost: Int,
    val productionRate: Int,
    val imageId: Int,
    val level: Int = 0,
    val displayName: Int
)