package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int,
    val name: String,
    val originalQuantity: Int,
    val currentQuantity: Int
)
