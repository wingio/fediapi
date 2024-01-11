package fediapi.mastodon.client

import io.ktor.client.request.parameter
import fediapi.Client
import fediapi.http.HttpResponse
import fediapi.Language
import fediapi.URL
import fediapi.http.paging.PageExtractor
import fediapi.http.paging.PageInfo
import fediapi.http.paging.PagedResponse
import fediapi.mastodon.constants.Routes
import fediapi.mastodon.constants.Scope
import fediapi.mastodon.model.Application
import fediapi.mastodon.model.Error
import fediapi.mastodon.model.Token
import fediapi.mastodon.model.account.Account
import fediapi.mastodon.model.account.CredentialAccount
import fediapi.mastodon.model.status.Status
import fediapi.mastodon.util.appendFieldsHash
import fediapi.ktor.appendFile
import fediapi.ktor.setForm
import fediapi.ktor.setFormField
import fediapi.mastodon.model.request.GrantType
import fediapi.mastodon.model.request.Privacy
import fediapi.mastodon.request.AccountRequests
import fediapi.mastodon.request.AppsRequests
import fediapi.mastodon.request.EmailRequests
import fediapi.mastodon.request.OauthRequests
import fediapi.mastodon.util.addPageParams

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
     * Register client applications that can be used to obtain OAuth tokens.
     */
    public val apps: AppsRequests = AppsRequests(this)

    /**
     * Generate and manage OAuth tokens.
     */
    public val oauth: OauthRequests = OauthRequests(this)

    /**
     * Request a new confirmation email, potentially to a new email address.
     */
    public val emails: EmailRequests = EmailRequests(this)

    /**
     * Methods concerning accounts and profiles.
     */
    public val accounts: AccountRequests = AccountRequests(this)

}