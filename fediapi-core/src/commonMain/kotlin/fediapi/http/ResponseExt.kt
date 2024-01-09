package fediapi.http

/**
 * Callbacks for every kind of response status
 */
public inline fun <T, E> HttpResponse<T, E>.fold(
    success: (T) -> Unit = {},
    empty: () -> Unit = {},
    error: (E?) -> Unit = {},
    failure: (ApiFailure) -> Unit = {}
): Unit = when (this) {
    is HttpResponse.Success -> success(data)
    is HttpResponse.Empty -> empty()
    is HttpResponse.Error -> error(this.error)
    is HttpResponse.Failure -> failure(this.error)
}

/**
 * Version of [fold] that treats both [HttpResponse.Error] and [HttpResponse.Failure] as an error
 */
public inline fun <T, E> HttpResponse<T, E>.fold(
    success: (T) -> Unit = {},
    fail: (E?) -> Unit = {},
    empty: () -> Unit = {}
): Unit = when (this) {
    is HttpResponse.Success -> success(data)
    is HttpResponse.Empty -> empty()
    is HttpResponse.Error -> fail(error)
    is HttpResponse.Failure -> fail(null)
}

/**
 * Only calls the provided [block] if the response was successful (returned data)
 */
public inline infix fun <T, E> HttpResponse<T, E>.ifSuccessful(block: (T) -> Unit) {
    if (this is HttpResponse.Success) block(data)
}

/**
 * Only calls the provided [block] if the response was empty (returned no data)
 */
public inline infix fun <T, E> HttpResponse<T, E>.ifEmpty(crossinline block: () -> Unit) {
    if (this is HttpResponse.Empty) block()
}

/**
 * If successful it returns the underlying [data][T] otherwise returns null
 */
public fun <T, E> HttpResponse<T, E>.getOrNull(): T? = when (this) {
    is HttpResponse.Success -> data
    is HttpResponse.Empty,
    is HttpResponse.Error,
    is HttpResponse.Failure -> null
}