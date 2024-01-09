@file:Suppress("FunctionName", "PropertyName")

package fediapi.mastodon.constants

import fediapi.Client

/**
 * All available Mastodon routes/endpoints, to be used by [Client.route]
 */
public object Routes {

    public object OAUTH {

        public const val AUTHORIZE: String = "/oauth/authorize"
        public const val TOKEN: String = "/oauth/token"
        public const val REVOKE: String = "/oauth/revoke"

    }

    public object V1 {

        public class Accounts(
            private val id: String
        ) {
            override fun toString(): String = "$Accounts/$id"

            public val Statuses: String = "$this/statuses"
            public val Followers: String = "$this/followers"
            public val Following: String = "$this/following"
            public val FeaturedTags: String = "$this/featured_tags"
            public val Lists: String = "$this/lists"
            public val Follow: String = "$this/follow"
            public val Unfollow: String = "$this/unfollow"
            public val RemoveFromFollowers: String = "$this/remove_from_followers"
            public val Block: String = "$this/block"
            public val Unblock: String = "$this/unblock"
            public val Mute: String = "$this/mute"
            public val Unmute: String = "$this/unmute"
            public val Pin: String = "$this/pin"
            public val Unpin: String = "$this/unpin"
            public val Note: String = "$this/note"
            @Deprecated("Route now returns an empty list") public val IdentityProofs: String = "$this/identity_proofs"

            public companion object {
                override fun toString(): String = "/api/v1/accounts"

                public val VerifyCredentials: String = "$Accounts/verify_credentials"
                public val UpdateCredentials: String = "$Accounts/update_credentials"
                public val Relationships: String = "$Accounts/relationships"
                public val FamiliarFollowers: String = "$Accounts/familiar_followers"
                public val Search: String = "$Accounts/search"
                public val Lookup: String = "$Accounts/lookup"
            }
        }

        public object Apps {
            override fun toString(): String = "/api/v1/apps"

            public val VerifyCredentials: String = "$Apps/verify_credentials"
        }

        public object Emails {
            override fun toString(): String = "/api/v1/emails"

            public val Confirmations: String = "$Emails/confirmations"
        }

    }

}