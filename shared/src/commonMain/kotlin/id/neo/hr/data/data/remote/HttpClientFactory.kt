package id.neo.hr.data.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient = HttpClient {
    expectSuccess = true

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            },
        )
    }

    // BODY dipakai sementara untuk observasi request/response saat development.
    // Jangan aktifkan level ini pada build production karena body dapat berisi data sensitif.
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Napier.d(message, tag = "HttpClient")
            }
        }
        level = LogLevel.ALL
//        sanitizeHeader { header ->
//            header.equals(HttpHeaders.Authorization, ignoreCase = true) || header.equals("token-generated", ignoreCase = true)
//        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 10_000L // 10 detik
        connectTimeoutMillis = 10_000L
        socketTimeoutMillis = 10_000L
    }


    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
    }
}
