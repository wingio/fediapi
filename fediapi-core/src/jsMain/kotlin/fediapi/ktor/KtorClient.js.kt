package fediapi.ktor

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

public actual val DefaultKtorClient: HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json(DefaultJsonClient)
    }
}