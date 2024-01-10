package fediapi.http.paging

/**
 * Represents info for a paged request
 *
 * @param since When present, will only fetch results after this id
 * @param min Returns results immediately newer than this ID. In effect, sets a cursor at this ID and paginates forward.
 * @param max All results returned will be lesser than this ID. In effect, sets an upper bound on results.
 */
public data class PageInfo(
    val since: String? = null,
    val min: String? = null,
    val max: String? = null
)