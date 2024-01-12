package fediapi.mastodon.client

import fediapi.Client
import fediapi.http.HttpResponse
import fediapi.http.paging.PageExtractor
import fediapi.http.paging.PagedResponse
import fediapi.mastodon.model.Error
import fediapi.mastodon.requests.account.AccountRequests
import fediapi.mastodon.requests.apps.AppRequests
import fediapi.mastodon.requests.apps.EmailRequests
import fediapi.mastodon.requests.apps.OauthRequests
import fediapi.mastodon.requests.account.BookmarkRequests

/**
 * Shortcut for an [HttpResponse] with Mastodon's standard [Error] model
 */
public typealias MastodonResponse<T> = HttpResponse<T, Error>

/**
 * Shortcut for a [PagedResponse] with Mastodon's standard [Error] model
 */
public typealias PagedMastodonResponse<T> = PagedResponse<T, Error>

/**
 * An "empty" response, actually just "{}"
 */
public typealias EmptyResponse = HttpResponse<String, Error>

/**
 * [Client] used to interact with the Mastodon API
 *
 * @param baseUrl The url of the instance to interact with
 * @param token The authorization token for a user or app, if unspecified then only public requests will be available.
 */
public class MastodonClient(
    baseUrl: String,
    token: String? = null
) : Client(
    baseUrl = baseUrl,
    token = if (token == null) null else "${if (!token.startsWith("Bearer")) "Bearer " else ""} $token"
) {

    override val pageExtractor: PageExtractor = LinkPageExtractor()

    /**
     * Methods concerning accounts and profiles.
     */
    public val accounts: AccountRequests = AccountRequests(this)

    /**
     * View your bookmarks.
     */
    public val bookmarks: BookmarkRequests = BookmarkRequests(this)

    /**
     * Register client applications that can be used to obtain OAuth tokens.
     */
    public val apps: AppRequests = AppRequests(this)

    /**
     * Request a new confirmation email, potentially to a new email address.
     */
    public val emails: EmailRequests = EmailRequests(this)

    /**
     * Generate and manage OAuth tokens.
     */
    public val oauth: OauthRequests = OauthRequests(this)

}