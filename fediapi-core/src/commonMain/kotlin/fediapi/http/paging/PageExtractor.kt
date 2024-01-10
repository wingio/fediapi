package fediapi.http.paging

import io.ktor.client.statement.*

/**
 * Used to extract data necessary for paging
 */
public interface PageExtractor {

    /**
     * Extracts the paging parameters from the [response]
     *
     * @return A pair containing the next and previous page infos, in that order
     */
    public fun getPageInfo(response: HttpResponse): Pair<PageInfo?, PageInfo?>

}