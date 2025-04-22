package com.example.services

import com.example.models.Item
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ItemStorage(private val filePath: String = "src/main/resources/items.json") {
    private val json = Json { prettyPrint = true }
    private var items: MutableList<Item> = mutableListOf()

    init {
        val file = File(filePath)
        items = if (file.exists()) {
            json.decodeFromString<List<Item>>(file.readText()).toMutableList()
        } else {
            mutableListOf()
        }
    }

    private fun saveToFile() {
        File(filePath).writeText(json.encodeToString(items))
    }

    fun getAll(): List<Item> = items

    fun buyItem(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return false
        val item = items[index]
        if (item.currentQuantity <= 0) return false
        items[index] = item.copy(currentQuantity = item.currentQuantity - 1)
        saveToFile()
        return true
    }

    fun resetAll() {
        items = items.map { it.copy(currentQuantity = it.originalQuantity) }.toMutableList()
        saveToFile()
    }
}
