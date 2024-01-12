package fediapi.ktor

import fediapi.VERSION
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

public actual val DefaultKtorClient: HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json(DefaultJsonClient)
    }
    defaultRequest {
        userAgent("FediAPI v$VERSION (js) - https://github.com/wingio/fediapi")
    }
}