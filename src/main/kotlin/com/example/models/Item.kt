package com.example.models

import kotlinx.serialization.Serializable

@Serializable                   // Marks this class as serializable for JSON conversion
data class Item(
    val id: Int,               // Unique identifier for the item
    val name: String,          // Name of the item
    val originalQuantity: Int, // Initial quantity of the item
    val currentQuantity: Int   // Current available quantity
)
