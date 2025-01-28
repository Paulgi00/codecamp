package com.example.wildidle.model

data class Item(
    val name: String,
    val cost: Float,
    val duration: Int? = null,
    val boostFactor: Int? = null,
    val unitPerSec: Int? = null,
    val multiplier: Float? = null
)