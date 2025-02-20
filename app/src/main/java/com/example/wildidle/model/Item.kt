package com.example.wildidle.model

import com.squareup.moshi.Json

// item object from server, which can be passive, boost, upgrade
data class Item(
    val name: String,
    val cost: Int,
    val duration: Int? = null,
    @Json(name = "boost-factor")
    val boostFactor: Double? = null,
    @Json(name = "unit/sec")
    val unitSec: Int? = null,
    val multiplier: Float? = null
)