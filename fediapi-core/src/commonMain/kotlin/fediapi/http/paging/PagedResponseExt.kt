package fediapi.http.paging

import fediapi.http.ApiFailure

/**
 * Callbacks for every kind of response status
 *
 * @param success Handler for when the request returned successfully
 * @param empty Handler for when the request returned an empty response
 * @param error Handler for when the request returned an error
 * @param failure Handler for any client-side errors, such as no internet or failed deserialization
 */
public inline fun <T, E> PagedResponse<T, E>.fold(
    success: (PagedResponse.Success<T, E>) -> Unit = {},
    empty: () -> Unit = {},
    error: (E?) -> Unit = {},
    failure: (ApiFailure) -> Unit = {}
): Unit = when (this) {
    is PagedResponse.Success -> success(this)
    is PagedResponse.Empty -> empty()
    is PagedResponse.Error -> error(this.error)
    is PagedResponse.Failure -> failure(this.error)
}

/**
 * Version of [fold] that treats both [PagedResponse.Error] and [PagedResponse.Failure] as an error
 *
 * @param success Handler for when the request returned successfully
 * @param empty Handler for when the request returned an empty response
 * @param fail Handler for any errors, both client and server side
 */
public inline fun <T, E> PagedResponse<T, E>.fold(
    success: (PagedResponse.Success<T, E>) -> Unit = {},
    fail: (E?) -> Unit = {},
    empty: () -> Unit = {}
): Unit = when (this) {
    is PagedResponse.Success -> success(this)
    is PagedResponse.Empty -> empty()
    is PagedResponse.Error -> fail(error)
    is PagedResponse.Failure -> fail(null)
}

/**
 * Callbacks for every kind of response status, provides the list of items without any additional paging
 *
 * @param success Handler for when the request returned successfully
 * @param empty Handler for when the request returned an empty response
 * @param error Handler for when the request returned an error
 * @param failure Handler for any client-side errors, such as no internet or failed deserialization
 */
public inline fun <T, E> PagedResponse<T, E>.foldPage(
    success: (List<T>) -> Unit = {},
    empty: () -> Unit = {},
    error: (E?) -> Unit = {},
    failure: (ApiFailure) -> Unit = {}
): Unit = when (this) {
    is PagedResponse.Success -> success(data)
    is PagedResponse.Empty -> empty()
    is PagedResponse.Error -> error(this.error)
    is PagedResponse.Failure -> failure(this.error)
}

/**
 * Version of [foldPage] that treats both [PagedResponse.Error] and [PagedResponse.Failure] as an error
 *
 * @param success Handler for when the request returned successfully
 * @param empty Handler for when the request returned an empty response
 * @param fail Handler for any errors, both client and server side
 */
public inline fun <T, E> PagedResponse<T, E>.foldPage(
    success: (List<T>) -> Unit = {},
    fail: (E?) -> Unit = {},
    empty: () -> Unit = {}
): Unit = when (this) {
    is PagedResponse.Success -> success(data)
    is PagedResponse.Empty -> empty()
    is PagedResponse.Error -> fail(error)
    is PagedResponse.Failure -> fail(null)
}

/**
 * Only calls the provided [block] if the response was successful (returned data)
 */
public inline infix fun <T, E> PagedResponse<T, E>.ifSuccessful(block: (PagedResponse.Success<T, E>) -> Unit) {
    if (this is PagedResponse.Success) block(this)
}

/**
 * Only calls the provided [block] if the response was successful (returned data)
 */
public inline infix fun <T, E> PagedResponse<T, E>.ifPageSuccessful(block: (List<T>) -> Unit) {
    if (this is PagedResponse.Success) block(data)
}

/**
 * Only calls the provided [block] if the response was empty (returned no data)
 */
public inline infix fun <T, E> PagedResponse<T, E>.ifEmpty(crossinline block: () -> Unit) {
    if (this is PagedResponse.Empty) block()
}

/**
 * If successful, returns the items, otherwise returns null
 *
 * @return [List] of [T] - The page of items, if successful
 */
public fun <T, E> PagedResponse<T, E>.getPageOrNull(): List<T>? = when (this) {
    is PagedResponse.Success -> data
    is PagedResponse.Empty,
    is PagedResponse.Error,
    is PagedResponse.Failure -> null
}

/**
 * If successful, returns the items, otherwise throws an error
 *
 * @throws NullPointerException If response is unsuccessful
 * @return [List] of [T] - The page of items
 */
public fun <T, E> PagedResponse<T, E>.getPageOrThrow(): List<T> =
    getPageOrNull() ?: throw NullPointerException("Response was not successful")

/**
 * If successful, returns the items, otherwise returns null
 *
 * @return [PagedResponse.Success] - The page of items alongside next and previous [PageInfo], if successful
 */
public fun <T, E> PagedResponse<T,E>.getOrNull(): PagedResponse.Success<T, E>? =
    this as? PagedResponse.Success

/**
 * If successful, returns the items, otherwise throws an error
 *
 * @throws NullPointerException If response is unsuccessful
 * @return [PagedResponse.Success] - The page of items alongside the next and previous [PageInfo]
 */
public fun <T, E> PagedResponse<T,E>.getOrThrow(): PagedResponse.Success<T, E> =
    getOrNull() ?: throw NullPointerException("Response was not successful")