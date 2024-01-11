package fediapi.mastodon.request

import fediapi.URL
import fediapi.ktor.setForm
import fediapi.mastodon.client.MastodonClient
import fediapi.mastodon.client.MastodonResponse
import fediapi.mastodon.constants.Routes
import fediapi.mastodon.constants.Scope
import fediapi.mastodon.model.Application

/**
 * Register client applications that can be used to obtain OAuth tokens.
 *
 * @param client Client used to make requests
 */
public class AppsRequests(
    private val client: MastodonClient
) {

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
    ): MastodonResponse<Application> = client.post(Routes.V1.Apps) {
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
    public suspend fun verifyCredentials(): MastodonResponse<Application> =
        client.get(Routes.V1.Apps.VerifyCredentials)

}