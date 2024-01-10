package fediapi.http.paging

import fediapi.http.ApiFailure

/**
 * Wrapper for paged api responses that enables better error handling, includes information that can be used to paginate through a list
 *
 * @param T The api model for the response list item
 * @param E The api model for an error response
 */
public sealed interface PagedResponse<out T, out E> {
    /**
     * [Data][data] was returned without any issues
     *
     * @param data The items in the current page
     * @param nextPage Information used to paginate forwards
     * @param previousPage Information used to paginate backwards
     */
    public data class Success<T, E>(
        val data: List<T>,
        val nextPage: PageInfo?,
        val previousPage: PageInfo?
    ) : PagedResponse<T, E>

    /**
     * Represents a response that has no body
     */
    public class Empty<T, E> : PagedResponse<T, E>

    /**
     * Represents an error returned by the server
     */
    public data class Error<T, E>(val error: E?) : PagedResponse<T, E>

    /**
     * Represents an error on the client (Ex. deserialization problem or device is offline)
     */
    public data class Failure<T, E>(val error: ApiFailure) : PagedResponse<T, E>
}