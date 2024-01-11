package fediapi.mastodon.request

import fediapi.ktor.setFormField
import fediapi.mastodon.client.EmptyResponse
import fediapi.mastodon.client.MastodonClient
import fediapi.mastodon.constants.Routes

/**
 * Request a new confirmation email, potentially to a new email address.
 *
 * @param client Client used to make requests
 */
public class EmailRequests(
    private val client: MastodonClient
) {

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
    ): EmptyResponse = client.post(Routes.V1.Emails.Confirmations) {
        if (email != null) setFormField("email", email)
    }

}