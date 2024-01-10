package fediapi.mastodon.client

import fediapi.http.paging.PageExtractor
import fediapi.http.paging.PageInfo
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Extracts paging info from the link response header
 *
 * Ex.
 * `<https://mastodon.example/api/v1/accounts/14715/followers?limit=2&max_id=7486869>; rel="next", <https://mastodon.example/api/v1/accounts/14715/followers?limit=2&since_id=7489740>; rel="prev"`
 */
public class LinkPageExtractor: PageExtractor {

    override fun getPageInfo(response: HttpResponse): Pair<PageInfo?, PageInfo?> {
        val linkHeader = response.headers["link"] ?: return null to null
        val links = linkHeader.split(", ")
        var next: PageInfo? = null
        var prev: PageInfo? = null

        links.forEach { link ->
            val match = LINK_REGEX.matchEntire(link)
            val url = match?.groupValues?.get(1)
            val rel = match?.groupValues?.get(2)

            when(rel) {
                "next" -> next = extractFromParams(url)
                "prev" -> prev = extractFromParams(url)
            }
        }

        return next to prev
    }

    /**
     * Extracts the necessary paging parameters from the [url]
     *
     * @param url Url returned inside the link header
     */
    private fun extractFromParams(url: String?): PageInfo? {
        if(url == null) return null
        val parsedUrl = Url(url)

        return PageInfo(
            since = parsedUrl.parameters["since_id"],
            min = parsedUrl.parameters["min_id"],
            max = parsedUrl.parameters["max_id"]
        )
    }

    internal companion object {

        val LINK_REGEX = "<(.+?)>; rel=\"(next|prev)\"".toRegex()

    }

}