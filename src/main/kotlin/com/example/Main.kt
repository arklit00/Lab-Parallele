package com.example

import com.example.routes.itemRoutes
import com.example.services.ItemStorage
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.response.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8081
    val host = System.getenv("HOST") ?: "0.0.0.0"

    embeddedServer(Netty, port = port, host = host) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val storage = ItemStorage()

    install(CORS) {
        anyHost()
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    routing {

        get("/swagger-ui/openapi.yaml") {
            val resource = this@module.environment.classLoader.getResource("swagger-ui/openapi.yaml")
            println("DEBUG: openapi.yaml resource = $resource")
            if (resource != null) {
                call.respondText(resource.readText(), ContentType.parse("application/yaml"))
            } else {
                call.respond(HttpStatusCode.NotFound, "openapi.yaml not found")
            }
        }

        // Serve the RapiDoc UI
        get("/docs") {
            call.respondText(
                """
            <!DOCTYPE html>
            <html>
              <head>
                <title>API Docs</title>
                <script type="module" src="https://unpkg.com/rapidoc/dist/rapidoc-min.js"></script>
              </head>
              <body>
                <rapi-doc
                  spec-url="/swagger-ui/openapi.yaml"
                  render-style="read"
                  theme="light"
                  show-header="true"
                  allow-spec-url-load="false"
                ></rapi-doc>
              </body>
            </html>
            """.trimIndent(),
                ContentType.Text.Html
            )
        }



        // Home UI
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
                                    const res = await fetch(`/items/buy/${'$'}{id}`, { method: 'POST' });
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
                    h1 { +"Shopping App" }
                    ul { id = "items" }
                    button {
                        onClick = "resetAll()"
                        +"Reset All"
                    }
                }
            }
        }

        // REST API routes
        itemRoutes(storage)
    }
}
