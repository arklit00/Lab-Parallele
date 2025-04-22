package com.example

import com.example.routes.itemRoutes
import com.example.services.ItemStorage
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val storage = ItemStorage()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    routing {
        // Web UI
        get("/") {
            call.respondHtml {
                head {
                    title { +"Shopping App" }
                    script {
                        unsafe {
                            +"""
                            async function loadItems() {
                                const res = await fetch('/items');
                                const items = await res.json();
                                const list = document.getElementById("items");
                                list.innerHTML = '';
                                items.forEach(item => {
                                    const li = document.createElement("li");
                                    li.innerHTML = `
                                        ${'$'}{item.name} — ${'$'}{item.currentQuantity}/${'$'}{item.originalQuantity}
                                        <button onclick="buyItem(${'$'}{item.id})">Buy</button>
                                    `;
                                    list.appendChild(li);
                                });
                            }

                           async function buyItem(id) {
                                const res = await fetch(`/items/buy/'$'{id}`, { method: 'POST' });
                                if (res.ok) {
                                    alert('Item bought successfully');
                                } else if (res.status === 400) {
                                const msg = await res.text();
                                alert('Cannot buy item: ' + msg);
                                } else {
                                    alert('Unexpected error occurred');
                                }
                                loadItems();
                            }
                           async function resetAll() {
                               const res = await fetch('/items/reset', { method: 'POST' });
                               if (res.ok) {
                                   alert('All quantities reset');
                               } else {
                               alert('Reset failed');
                               }
                               loadItems();
                           }
                            window.onload = loadItems;
                            """.trimIndent()
                        }
                    }
                }
                body {
                    h1 { +"Simple Shopping App" }

                    ul { id = "items" }

                    button {
                        onClick = "resetAll()"
                        +"Reset All"
                    }
                }
            }
        }

        // REST API
        itemRoutes(storage)
    }
}
