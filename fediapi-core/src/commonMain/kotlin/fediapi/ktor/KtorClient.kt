package fediapi.ktor

import io.ktor.client.HttpClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
public val DefaultJsonClient: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

public expect val DefaultKtorClient: HttpClient