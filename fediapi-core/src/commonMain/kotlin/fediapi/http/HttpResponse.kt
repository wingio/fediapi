package fediapi.http

import io.ktor.http.HttpStatusCode

/**
 * Wrapper for api responses that enables better error handling
 *
 * @param T The api model for the response
 * @param E The api model for an error response
 */
public sealed interface HttpResponse<out T, out E> {
    /**
     * [Data][data] was returned without any issues
     */
    public data class Success<T, E>(val data: T) : HttpResponse<T, E>

    /**
     * Represents a response that has no body
     */
    public class Empty<T, E> : HttpResponse<T, E>

    /**
     * Represents an error returned by the server
     */
    public data class Error<T, E>(val error: E?) : HttpResponse<T, E>

    /**
     * Represents an error on the client (Ex. deserialization problem or device is offline)
     */
    public data class Failure<T, E>(val error: ApiFailure) : HttpResponse<T, E>
}

/**
 * Contains the error information returned by the server
 *
 * @param code A valid HTTP status code
 * @param body The response body as text
 */
public class ApiError(code: HttpStatusCode, body: String?) : Error("HTTP Code $code, Body: $body")

/**
 * Contains the exact error caused by the client
 *
 * @param error The error thrown during the request
 * @param body The response body (Only available if the failure occurred after a successful request, usually due to an incorrect api model)
 */
public class ApiFailure(error: Throwable, body: String?) : Error(body, error)