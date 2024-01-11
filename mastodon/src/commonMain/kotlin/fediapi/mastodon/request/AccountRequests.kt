package fediapi.mastodon.request

import fediapi.Language
import fediapi.http.paging.PageInfo
import fediapi.ktor.appendFile
import fediapi.ktor.setForm
import fediapi.mastodon.client.MastodonClient
import fediapi.mastodon.client.MastodonResponse
import fediapi.mastodon.client.PagedMastodonResponse
import fediapi.mastodon.constants.Routes
import fediapi.mastodon.constants.Scope
import fediapi.mastodon.model.FeaturedTag
import fediapi.mastodon.model.Relationship
import fediapi.mastodon.model.Token
import fediapi.mastodon.model.UserList
import fediapi.mastodon.model.account.Account
import fediapi.mastodon.model.account.CredentialAccount
import fediapi.mastodon.model.request.Privacy
import fediapi.mastodon.model.status.Status
import fediapi.mastodon.util.addPageParams
import fediapi.mastodon.util.appendFieldsHash
import io.ktor.client.request.*

/**
 * Methods concerning accounts and profiles.
 *
 * @param client Client used to make requests
 */
public class AccountRequests(
    private val client: MastodonClient
) {

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
    ): MastodonResponse<Token> = client.post(Routes.V1.Accounts) {
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
    public suspend fun verifyCredentials(): MastodonResponse<CredentialAccount> =
        client.get(Routes.V1.Accounts.VerifyCredentials)

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
    public suspend fun updateAccount(
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
    ): MastodonResponse<CredentialAccount> = client.patch(Routes.V1.Accounts.UpdateCredentials) {
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
        accountId: String
    ): MastodonResponse<Account> = client.get(Routes.V1.Accounts(accountId))

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
    ): PagedMastodonResponse<Status> = client.paged(Routes.V1.Accounts(accountId).Statuses) {
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
     * @param accountId The id of the [Account] to check.
     * @param pageInfo Information about which page to return.
     * @param limit Maximum number of results to return (Max 80).
     *
     * @return Page of [Account]
     */
    public suspend fun getAccountFollowers(
        accountId: String,
        pageInfo: PageInfo? = null,
        limit: Int? = 40
    ): PagedMastodonResponse<Account> = client.paged(Routes.V1.Accounts(accountId).Followers) {
        addPageParams(pageInfo)
        parameter("limit", limit)
    }

    /**
     * Accounts which the given account is following, if network is not hidden by the account owner.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#following)
     *
     * @param accountId The id of the [Account] to check.
     * @param pageInfo Information about which page to return.
     * @param limit Maximum number of results per page (Max 80).
     *
     * @return Page of [Account]
     */
    public suspend fun getAccountFollowing(
        accountId: String,
        pageInfo: PageInfo? = null,
        limit: Int? = 40
    ): PagedMastodonResponse<Account> = client.paged(Routes.V1.Accounts(accountId).Following) {
        addPageParams(pageInfo)
        parameter("limit", limit)
    }

    /**
     * Tags featured by this account.
     *
     * **Required scopes**: None
     *
     * **Authorization**: Public
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#featured_tags)
     *
     * @param accountId The id of the desired [Account] to check.
     *
     * @return List of [FeaturedTag]
     */
    public suspend fun getAccountFeaturedTags(
        accountId: String
    ): MastodonResponse<List<FeaturedTag>> = client.get(Routes.V1.Accounts(accountId).FeaturedTags)

    /**
     * User lists that you have added this account to.
     *
     * **Required scopes**: [read:lists][Scope.Read.Lists]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#lists)
     *
     * @param accountId The id of the desired [Account] to check.
     *
     * @return List of [UserList]
     */
    public suspend fun getListsWithAccount(
        accountId: String
    ): MastodonResponse<List<UserList>> = client.get(Routes.V1.Accounts(accountId).Lists)

    /**
     * Follow the given account. Can also be used to update whether to show reblogs or enable notifications.
     *
     * **Required scopes**: [write:follows][Scope.Write.Follows]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#follow)
     *
     * @param accountId The id of the desired [Account] to follow.
     * @param reblogs Receive this account’s reblogs in home timeline?
     * @param notify Receive notifications when this account posts a status?
     * @param languages Filter received statuses for these languages. If not provided, you will receive this account’s posts in all languages.
     *
     * @return [Relationship]
     */
    public suspend fun followAccount(
        accountId: String,
        reblogs: Boolean = true,
        notify: Boolean = false,
        languages: List<Language>? = null
    ): MastodonResponse<Relationship> = client.post(Routes.V1.Accounts(accountId).Follow) {
        setForm {
            append("reblogs", reblogs)
            append("notify", notify)
            languages?.let {
                append("languages[]", languages)
            }
        }
    }

    /**
     * Unfollow the given account.
     *
     * **Required scopes**: [write:follows][Scope.Write.Follows]
     *
     * **Authorization**: User
     *
     * [More](https://docs.joinmastodon.org/methods/accounts/#unfollow)
     *
     * @param accountId The id of the desired [Account] to unfollow.
     *
     * @return [Relationship]
     */
    public suspend fun unfollowAccount(
        accountId: String
    ): MastodonResponse<Relationship> = client.post(Routes.V1.Accounts(accountId).Unfollow)

}