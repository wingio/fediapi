package fediapi

import fediapi.http.ApiFailure
import fediapi.http.HttpResponse
import fediapi.http.paging.PageExtractor
import fediapi.http.paging.PagedResponse
import fediapi.ktor.DefaultJsonClient
import fediapi.ktor.DefaultKtorClient
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Default dispatcher to use when sending api requests
 */
public expect val DefaultDispatcher: CoroutineDispatcher

/**
 * Base class for creating a client that can interact with a fediverse platform's API
 *
 * @param baseUrl
 * @param ktorClient
 */
public abstract class Client(
    baseUrl: String,
    token: String? = null,
    json: Json = DefaultJsonClient,
    ktorClient: HttpClient = DefaultKtorClient
) {

    /**
     * The base url used to make requests.
     */
    public var baseUrl: String = baseUrl
        private set

    /**
     * The [HttpClient] used to execute requests.
     */
    public var ktorClient: HttpClient = ktorClient
        private set

    /**
     * The json config used to deserialize api models.
     */
    public var json: Json = json
        private set

    /**
     * The token used to authorize requests, not required for all requests.
     */
    public var token: String? = token
        private set

    /**
     * Used to extract information that can be used for paging.
     */
    public abstract val pageExtractor: PageExtractor

    /**
     * Sets the base url for this engine
     *
     * @param url The new url to use
     */
    public fun baseUrl(url: String) {
        baseUrl = if(url.startsWith("http")) url else "https://${url}"
    }

    /**
     * Configures the underlying [HttpClient] with the [config] block
     */
    public fun configureKtor(
        config: HttpClientConfig<*>.() -> Unit
    ) {
        ktorClient = ktorClient.config(config)
    }

    /**
     * Executes a request for the given [route]
     *
     * @see get
     * @see post
     * @see patch
     * @see put
     * @see delete
     * @see paged
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param method The method to use for this request, defaults to [GET][HttpMethod.Get]
     * @param request Configuration for the request, this is where a body would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> route(
        route: Any,
        method: HttpMethod = HttpMethod.Get,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> {
        return withContext(DefaultDispatcher) {
            var body: String? = null // Holds the raw body text, in case of any failures

            val response = try {
                val response = ktorClient.request {
                    url("$baseUrl$route")
                    this.method = method
                    token?.let {
                        header(HttpHeaders.Authorization, token) // Applies the authorization header only if a token is present
                    }
                    request() // Apply the users desired configuration
                }

                if (response.status.isSuccess()) { // Status code in the 2XX range
                    body = response.bodyAsText() // Save for deserialization

                    when {
                        response.status == HttpStatusCode.NoContent -> HttpResponse.Empty<T, E>() // Nothing is returned

                        T::class.instanceOf(String::class) -> HttpResponse.Success(body as T) // Return the raw body if type T is a string

                        else -> HttpResponse.Success(json.decodeFromString<T>(body)) // Otherwise, attempt to deserialize into type T
                    }
                } else { // Server returned an error
                    body = response.bodyAsText() // Save for deserialization

                    when {
                        response.status == HttpStatusCode.Gone -> HttpResponse.Empty() // Nothing is returned

                        E::class.instanceOf(String::class) -> HttpResponse.Error(body as E) // Return the raw body if the error type is a string

                        else -> HttpResponse.Error(json.decodeFromString<E>(body)) // Otherwise, attempt to deserialize into the error type
                    }
                }
            } catch (e: Throwable) {
                // Something went wrong on our end, usually due to an inaccurate api model
                // Make an issue here if it's with a preset: https://github.com/wingio/fediapi/issues/new
                HttpResponse.Failure(ApiFailure(e, body))
            }

            return@withContext response
        }
    }

    /**
     * Requests a list of items that can be paged through
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param method The method to use for this request, defaults to [GET][HttpMethod.Get]
     * @param request Configuration for the request, this is where a body would be specified
     *
     * @return [PagedResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> paged(
        route: Any,
        method: HttpMethod = HttpMethod.Get,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): PagedResponse<T, E> {
        return withContext(DefaultDispatcher) {
            var body: String? = null // Holds the raw body text, in case of any failures

            val response = try {
                val response = ktorClient.request {
                    url("$baseUrl$route")
                    this.method = method
                    token?.let {
                        header(HttpHeaders.Authorization, token) // Applies the authorization header only if a token is present
                    }
                    request() // Apply the users desired configuration
                }

                if (response.status.isSuccess()) { // Status code in the 2XX range
                    body = response.bodyAsText() // Save for deserialization

                    when {
                        response.status == HttpStatusCode.NoContent -> PagedResponse.Empty<T, E>() // Nothing is returned

                        else -> { // Otherwise, attempt to deserialize into a list of type T
                            val (nextPage, previousPage) = pageExtractor.getPageInfo(response)
                            PagedResponse.Success(json.decodeFromString<List<T>>(body), nextPage, previousPage)
                        }
                    }
                } else { // Server returned an error
                    body = response.bodyAsText() // Save for deserialization

                    when {
                        response.status == HttpStatusCode.Gone -> PagedResponse.Empty() // Nothing is returned

                        E::class.instanceOf(String::class) -> PagedResponse.Error(body as E) // Return the raw body if the error type is a string

                        else -> PagedResponse.Error(json.decodeFromString<E>(body)) // Otherwise, attempt to deserialize into the error type
                    }
                }
            } catch (e: Throwable) {
                // Something went wrong on our end, usually due to an inaccurate api model
                // Make an issue here if it's with a preset: https://github.com/wingio/fediapi/issues/new
                PagedResponse.Failure(ApiFailure(e, body))
            }

            return@withContext response
        }
    }

    /**
     * Performs a GET request on the specified [route]
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param request Configuration for the request, this is where url parameters would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> get(
        route: Any,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> = route(route, HttpMethod.Get, request)

    /**
     * Performs a POST request on the specified [route]
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param request Configuration for the request, this is where the body would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> post(
        route: Any,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> = route(route, HttpMethod.Post, request)

    /**
     * Performs a PATCH request on the specified [route]
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param request Configuration for the request, this is where the body would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> patch(
        route: Any,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> = route(route, HttpMethod.Patch, request)

    /**
     * Performs a PUT request on the specified [route]
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param request Configuration for the request, this is where the body would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> put(
        route: Any,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> = route(route, HttpMethod.Put, request)

    /**
     * Performs a DELETE request on the specified [route]
     *
     * @param route The route to be requested, not a full url. Although this parameter can be any type it will be converted to a [String]
     * @param request Configuration for the request, this is where the body would be specified
     *
     * @return [HttpResponse] with the desired return and error types, to be deserialized into.
     */
    public suspend inline fun <reified T, reified E> delete(
        route: Any,
        crossinline request: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse<T, E> = route(route, HttpMethod.Delete, request)

}