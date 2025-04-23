package com.example.routes

import com.example.services.ItemStorage
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.itemRoutes(storage: ItemStorage) {

    route("/items") {

        // GET /items - Return all items as JSON
        get {
            call.respond(storage.getAll())
        }

        // POST /items/buy/{id} - Attempt to buy an item by ID (decrement quantity)
        post("/buy/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                // Respond with 400 if ID is invalid or missing
                call.respond(HttpStatusCode.BadRequest, "Invalid item ID")
                return@post
            }
            val success = storage.buyItem(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Item bought successfully")
            } else {
                // Respond with 400 if item is out of stock or unavailable
                call.respond(HttpStatusCode.BadRequest, "Item not available or out of stock")
            }
        }

        // POST /items/reset - Reset all item quantities to their original values
        post("/reset") {
            storage.resetAll()
            call.respond(HttpStatusCode.OK, "All item quantities reset")
        }
    }
}
