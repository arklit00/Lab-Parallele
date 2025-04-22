package com.example.routes

import com.example.models.Item
import com.example.services.ItemStorage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.html.*
import kotlinx.html.*

fun Route.itemRoutes(storage: ItemStorage) {

    route("/items") {

        // Get all items
        get {
            call.respond(storage.getAll())
        }

        // Buy an item (decrement quantity if > 0)
        post("/buy/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid item ID")
                return@post
            }
            val success = storage.buyItem(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Item bought successfully")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Item not available or out of stock")
            }
        }

        // Reset all items quantities to original
        post("/reset") {
            storage.resetAll()
            call.respond(HttpStatusCode.OK, "All item quantities reset")
        }
    }
}
