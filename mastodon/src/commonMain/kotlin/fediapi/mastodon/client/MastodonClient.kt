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

    //  ############################################
    //  ###                Apps                  ###
    //  ############################################

    /**
     * Create a new application to obtain OAuth2 credentials.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * @param clientName A name for your application
     * @param redirectUris Where the user should be redirected after authorization. To display the authorization code to the user instead of redirecting to a web page, use `urn:ietf:wg:oauth:2.0:oob` in this parameter.
     * @param scopes List of OAuth scopes, see [Scope]
     * @param website A URL to the homepage of your app
     *
     * @return [Application] - The created application
     */
    public suspend fun createApplication(
        clientName: String,
        redirectUris: String,
        scopes: List<String> = listOf(Scope.Read()),
        website: URL? = null
    ): MastodonResponse<Application> = post(Routes.V1.Apps) {
        setForm {
            append("client_name", clientName)
            append("redirect_uris", redirectUris)
            append("scopes", scopes.joinToString(" "))
            website?.let { append("website", website) }
        }
    }

    /**
     * Confirm that the app’s OAuth2 credentials work.
     *
     * **Required scopes**: [read][Scope.Read]
     *
     * **Authorization**: App
     *
     * @return [Application], but without [clientId][Application.clientId] or [clientSecret][Application.clientSecret]
     */
    public suspend fun verifyAppCredentials(): MastodonResponse<Application> =
        get(Routes.V1.Apps.VerifyCredentials)


    //  #############################################
    //  ###                OAuth                  ###
    //  #############################################

    /**
     * Obtain an access token, to be used during API calls that are not public.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * @param grantType Set to [GrantType.CODE] if [code] is provided in order to gain user-level access. Otherwise, set to [GrantType.APP] to obtain app-level access only.
     * @param code A user authorization code, obtained via [GET /oauth/authorize](https://docs.joinmastodon.org/methods/oauth/#authorize).
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param redirectUri Set a URI to redirect the user to. If this parameter is set to `urn:ietf:wg:oauth:2.0:oob` then the token will be shown instead. Must match one of the `redirectUris` declared during app registration.
     * @param scope List of requested OAuth scopes. If [code] was provided, then this must be equal to the scope requested from the user. Otherwise, it must be a subset of scopes declared during app registration. If not provided, defaults to read.
     *
     * @return [Token] Credentials that can be used for authorized requests, make sure to store this for later.
     */
    public suspend fun getToken(
        grantType: GrantType,
        code: String? = null,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        scope: List<String> = listOf(Scope.Read())
    ): MastodonResponse<Token> = post(Routes.OAUTH.TOKEN) {
        setForm {
            append("grant_type", grantType.value)
            if (grantType == GrantType.CODE) {
                append("code",
                    code
                        ?: error("Code parameter must be provided when using MastodonGrantType.CODE")
                )
            }
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("redirect_uri", redirectUri)
            append("scope", scope.joinToString(" "))
        }
    }

    /**
     * Revoke an access token to make it no longer valid for use.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * @param clientId The client ID, obtained during app registration.
     * @param clientSecret The client secret, obtained during app registration.
     * @param token The previously obtained token, to be invalidated.
     *
     * @return A blank object as a [String] - "{}"
     */
    public suspend fun revokeToken(
        clientId: String,
        clientSecret: String,
        token: String
    ): EmptyResponse = post(Routes.OAUTH.REVOKE) {
        setForm {
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("token", token)
        }
    }


    //  ############################################
    //  ###               Emails                 ###
    //  ############################################

    /**
     * Resends the email used to verify the account, only works if the user has yet to confirm their email.
     *
     * **Required scopes**: None
     *
     * **Authorization**: User
     *
     * @param email If provided, updates the unconfirmed user’s email before resending the confirmation email.
     *
     * @return A blank object as a [String] - "{}"
     */
    public suspend fun resendConfirmationEmail(
        email: String? = null
    ): EmptyResponse = post(Routes.V1.Emails.Confirmations) {
        if (email != null) setFormField("email", email)
    }


    //  ############################################
    //  ###              Accounts                ###
    //  ############################################

    /**
     * Creates a user and account records.
     *
     * **Required scopes**: [write:accounts][Scope.Write.Accounts]
     *
     * **Authorization**: App
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#create)
     *
     * @param username The desired username for the account
     * @param email The email address to be used for login
     * @param password The password to be used for login
     * @param agreement  Whether the user agrees to the local rules, terms, and policies. These should be presented to the user in order to allow them to consent before setting this parameter to `true`.
     * @param locale The language of the confirmation email that will be sent.
     * @param reason If registrations require manual approval, this text will be reviewed by moderators.
     *
     * @return [Token] - Account access token for the app that initiated the request. This should be saved for later, and won't function until the user confirms their email.
     */
    public suspend fun registerAccount(
        username: String,
        email: String,
        password: String,
        agreement: Boolean,
        locale: Language = "en-US",
        reason: String? = null
    ): MastodonResponse<Token> = post(Routes.V1.Accounts) {
        setForm {
            append("username", username)
            append("email", email)
            append("password", password)
            append("agreement", agreement)
            append("locale", locale)
            reason?.let { append("reason", reason) }
        }
    }

    /**
     * Test to make sure that the user token works.
     *
     * **Required scopes**: [read:accounts][Scope.Read.Accounts]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#verify_credentials)
     *
     * @return [CredentialAccount] - The currently authorized account
     */
    public suspend fun verifyAccountCredentials(): MastodonResponse<CredentialAccount> =
        get(Routes.V1.Accounts.VerifyCredentials)

    /**
     * Update the user’s display and preferences.
     *
     * **Required scopes**: [write:accounts][Scope.Write.Accounts]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#update_credentials)
     *
     * @param displayName The display name to use for the profile.
     * @param note The account bio.
     * @param avatar Image bytes for the updated avatar
     * @param header Image bytes for the updated header
     * @param locked Whether manual approval of follow requests is required.
     * @param bot Whether the account has a bot flag.
     * @param discoverable Whether the account should be shown in the profile directory.
     * @param hideCollections Whether to hide followers and followed accounts.
     * @param indexable Whether public posts should be searchable to anyone.
     * @param fields New fields to display on a profile
     * @param statusPrivacy Default post privacy for authored statuses.
     * @param statusSensitive Whether to mark authored statuses as sensitive by default.
     * @param statusLanguage Default language to use for authored statuses (ISO 6391)
     *
     * @return [CredentialAccount]
     */
    public suspend fun updateAccountCredentials(
        displayName: String? = null,
        note: String? = null,
        avatar: ByteArray? = null,
        header: ByteArray? = null,
        locked: Boolean? = null,
        bot: Boolean? = null,
        discoverable: Boolean? = null,
        hideCollections: Boolean? = null,
        indexable: Boolean? = null,
        fields: Map<String, String>? = null,
        statusPrivacy: Privacy? = null,
        statusSensitive: Boolean? = null,
        statusLanguage: Language? = null
    ): MastodonResponse<CredentialAccount> = patch(Routes.V1.Accounts.UpdateCredentials) {
        setForm {
            displayName?.let { append("display_name", displayName) }
            note?.let { append("note", note) }
            avatar?.let {
                appendFile("avatar", avatar)
            }
            header?.let {
                appendFile("header", header)
            }
            locked?.let { append("locked", locked) }
            bot?.let { append("bot", bot) }
            discoverable?.let { append("discoverable", discoverable) }
            hideCollections?.let { append("hide_collections", hideCollections) }
            indexable?.let { append("indexable", indexable) }
            fields?.let { appendFieldsHash(fields) }
            statusPrivacy?.let { append("source[privacy]", statusPrivacy.value) }
            statusSensitive?.let { append("source[sensitive]", statusSensitive) }
            statusLanguage?.let { append("source[language]", statusLanguage) }
        }
    }

    /**
     * View information about a profile.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#get)
     *
     * @return [Account]
     */
    public suspend fun getAccount(
        id: String
    ): MastodonResponse<Account> = get(Routes.V1.Accounts(id))

    /**
     * Statuses posted to the given account.
     *
     * **Required scopes**: [read:statuses][Scope.Read.Statuses] for privated statuses
     *
     * **Authorization**: Public, User (Privated statuses)
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#statuses)
     *
     * @param accountId The ID of the [Account] who created the statuses.
     * @param pageInfo Information about which page to return.
     * @param limit Maximum number of results to return. Defaults to 20 statuses.
     * @param onlyMedia Filter out statuses without attachments.
     * @param excludeReplies Filter out statuses in reply to a different account.
     * @param excludeReblogs Filter out boosts from the response.
     * @param pinned Filter for pinned statuses only. Defaults to `false`, which includes all statuses. Pinned statuses do not receive special priority in the order of the returned results.
     * @param tagged Filter for statuses using a specific hashtag.
     *
     * @return Page of [Status]
     */
    public suspend fun getAccountStatuses(
        accountId: String,
        pageInfo: PageInfo? = null,
        limit: Int? = 20,
        onlyMedia: Boolean? = null,
        excludeReplies: Boolean? = null,
        excludeReblogs: Boolean? = null,
        pinned: Boolean? = false,
        tagged: String? = null
    ): PagedMastodonResponse<Status> = paged(Routes.V1.Accounts(accountId).Statuses) {
        addPageParams(pageInfo)
        parameter("limit", limit)
        parameter("only_media", onlyMedia)
        parameter("exclude_replies", excludeReplies)
        parameter("exclude_replies", excludeReblogs)
        parameter("pinned", pinned)
        parameter("tagged", tagged)
    }

    /**
     * Accounts which follow the given account, if network is not hidden by the account owner.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#followers)
     *
     * @param id The ID of the [Account] in the database.
     * @param pageInfo Information about which page to return.
     * @param limit Maximum number of results to return (Max 80).
     *
     * @return Page of [Account]
     */
    public suspend fun getAccountFollowers(
        id: String,
        pageInfo: PageInfo? = null,
        limit: Int? = 40,
    ): PagedMastodonResponse<Account> = paged(Routes.V1.Accounts(id).Followers) {
        addPageParams(pageInfo)
        parameter("limit", limit)
    }

}