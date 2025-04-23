package com.example.services

import com.example.models.Item
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStreamReader

class ItemStorage(private val filePath: String = "items.json") {

    // JSON parser with pretty print enabled
    private val json = Json { prettyPrint = true }

    // Internal list of items loaded from file or classpath
    private var items: MutableList<Item> = mutableListOf()

    // Flag to check if we can write to the items.json file
    private val isWritable: Boolean

    init {
        val externalFile = File(filePath)

        // Try to determine if the file is writable: either it exists or we can create it
        isWritable = try {
            if (!externalFile.exists()) {
                externalFile.createNewFile() // create empty file if it doesn't exist
                false // fallback to classpath loading (it's empty)
            } else {
                true // file exists and is likely writable
            }
        } catch (e: Exception) {
            false // failed to access file, fallback to read-only mode
        }

        // Load items from the file if writable and not empty, otherwise from classpath
        items = try {
            if (isWritable && externalFile.readText().isNotBlank()) {
                val fileText = externalFile.readText()
                json.decodeFromString<List<Item>>(fileText).toMutableList()
            } else {
                // Load default items.json from resources (inside JAR or build)
                val inputStream = javaClass.classLoader.getResourceAsStream("items.json")
                    ?: throw IllegalStateException("items.json not found in classpath")
                val text = InputStreamReader(inputStream).readText()
                json.decodeFromString<List<Item>>(text).toMutableList()
            }
        } catch (e: Exception) {
            println("❌ Failed to load items: ${e.message}")
            mutableListOf() // fallback to empty list on failure
        }

        println("✅ Loaded ${items.size} items")
    }

    // Writes the current items list to disk (only if writable)
    private fun saveToFile() {
        if (isWritable) {
            try {
                File(filePath).writeText(json.encodeToString(items))
            } catch (e: Exception) {
                println("❌ Failed to save items: ${e.message}")
            }
        } else {
            println("⚠️ Not writing to items.json (read-only mode or classpath resource)")
        }
    }

    // Returns all items
    fun getAll(): List<Item> = items

    // Decreases quantity of an item by 1 if available, and saves the change
    fun buyItem(id: Int): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1) return false // item not found
        val item = items[index]
        if (item.currentQuantity <= 0) return false // out of stock
        items[index] = item.copy(currentQuantity = item.currentQuantity - 1)
        saveToFile()
        return true
    }

    // Resets all items to their original quantity and saves
    fun resetAll() {
        items = items.map { it.copy(currentQuantity = it.originalQuantity) }.toMutableList()
        saveToFile()
    }
}
