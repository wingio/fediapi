package fediapi.mastodon.requests.account

import fediapi.http.paging.PageInfo
import fediapi.mastodon.client.MastodonClient
import fediapi.mastodon.client.PagedMastodonResponse
import fediapi.mastodon.constants.Routes
import fediapi.mastodon.constants.Scope
import fediapi.mastodon.model.status.Status
import fediapi.mastodon.util.addPageParams
import io.ktor.client.request.*

/**
 * View your bookmarks.
 */
public class BookmarkRequests(
    private val client: MastodonClient
) {

    /**
     * Statuses the user has bookmarked.
     *
     * **Required scopes**: [read:bookmarks][Scope.Read.Bookmarks]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/bookmarks/#get)
     *
     * @return Page of [Status]
     */
    public suspend fun getBookmarks(
        pageInfo: PageInfo? = null,
        limit: Int = 20
    ): PagedMastodonResponse<Status> = client.paged(Routes.V1.Bookmarks) {
        addPageParams(pageInfo)
        parameter("limit", limit)
    }

}