package fediapi.mastodon.requests.apps

import fediapi.ktor.setForm
import fediapi.mastodon.client.EmptyResponse
import fediapi.mastodon.client.MastodonClient
import fediapi.mastodon.client.MastodonResponse
import fediapi.mastodon.constants.Routes
import fediapi.mastodon.constants.Scope
import fediapi.mastodon.model.Token
import fediapi.mastodon.model.request.GrantType

/**
 * Generate and manage OAuth tokens.
 *
 * @param client Client used to make requests
 */
public class OauthRequests(
    private val client: MastodonClient
) {

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
    ): MastodonResponse<Token> = client.post(Routes.OAUTH.TOKEN) {
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
    ): EmptyResponse = client.post(Routes.OAUTH.REVOKE) {
        setForm {
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("token", token)
        }
    }

}