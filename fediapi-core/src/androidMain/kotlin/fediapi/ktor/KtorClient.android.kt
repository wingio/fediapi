package fediapi.ktor

import fediapi.VERSION
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

public actual val DefaultKtorClient: HttpClient = HttpClient(OkHttp) {
    install (ContentNegotiation) {
        json(DefaultJsonClient)
    }
    defaultRequest {
        userAgent("FediAPI v$VERSION (android) - https://github.com/wingio/fediapi")
    }
}