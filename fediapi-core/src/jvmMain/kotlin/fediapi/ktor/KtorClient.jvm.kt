package fediapi.ktor

import fediapi.VERSION
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

public actual val DefaultKtorClient: HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(DefaultJsonClient)
    }
    defaultRequest {
        userAgent("FediAPI v${VERSION} (jvm) - https://github.com/wingio/fediapi")
    }
}