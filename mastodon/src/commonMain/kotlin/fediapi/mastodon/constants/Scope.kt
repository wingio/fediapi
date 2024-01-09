@file:Suppress("ConstPropertyName")

package fediapi.mastodon.constants

/**
 * Container for all supported Mastodon OAuth scopes
 */
public object Scope {

    /**
     * Grants access to read data. Can be invoked as a function.
     */
    public object Read {

        public operator fun invoke(): String = "read"

        public val Accounts: String = "${Read()}:accounts"
        public val Blocks: String = "${Read()}:blocks"
        public val Bookmarks: String = "${Read()}:bookmarks"
        public val Favorites: String = "${Read()}:favourites"
        public val Filters: String = "${Read()}:filters"
        public val Follows: String = "${Read()}:follows"
        public val Lists: String = "${Read()}:lists"
        public val Mutes: String = "${Read()}:mutes"
        public val Notifications: String = "${Read()}:notifications"
        public val Search: String = "${Read()}:search"
        public val Statuses: String = "${Read()}:statuses"

    }

    /**
     * Grants access to write data. Can be invoked as a function.
     */
    public object Write {

        public operator fun invoke(): String = "write"

        public val Accounts: String = "${Write()}:accounts"
        public val Blocks: String = "${Write()}:blocks"
        public val Bookmarks: String = "${Write()}:bookmarks"
        public val Conversations: String = "${Write()}:conversations"
        public val Favorites: String = "${Write()}:favourites"
        public val Filters: String = "${Write()}:filters"
        public val Follows: String = "${Write()}:follows"
        public val Lists: String = "${Write()}:lists"
        public val Media: String = "${Write()}:media"
        public val Mutes: String = "${Write()}:mutes"
        public val Notifications: String = "${Write()}:notifications"
        public val Reports: String = "${Write()}:reports"
        public val Statuses: String = "${Write()}:statuses"

    }

    /**
     * Used for moderation API. Can **not** be invoked as a function.
     */
    public object Admin {

        public object Read {

            public operator fun invoke(): String = "admin:read"

            public val Accounts: String = "${Read()}:accounts"
            public val Reports: String = "${Read()}:reports"
            public val DomainAllows: String = "${Read()}:domain_allows"
            public val DomainBlocks: String = "${Read()}:domain_blocks"
            public val IpBlocks: String = "${Read()}:ip_blocks"
            public val EmailDomainBlocks: String = "${Read()}:email_domain_blocks"
            public val CanonicalEmailBlocks: String = "${Read()}:canonical_email_blocks"

        }

        public object Write {

            public operator fun invoke(): String = "admin:write"

            public val Accounts: String = "${Write()}:accounts"
            public val Reports: String = "${Write()}:reports"
            public val DomainAllows: String = "${Write()}:domain_allows"
            public val DomainBlocks: String = "${Write()}:domain_blocks"
            public val IpBlocks: String = "${Write()}:ip_blocks"
            public val EmailDomainBlocks: String = "${Write()}:email_domain_blocks"
            public val CanonicalEmailBlocks: String = "${Write()}:canonical_email_blocks"

        }

    }

    @Deprecated(
        message = "Deprecated since Mastodon 3.5.0",
        replaceWith = ReplaceWith(
            """
                Read.Blocks
                Write.Blocks
                Read.Follows
                Write.Follows
                Read.Mutes
                Write.Mutes
            """
        )
    )
    /**
     * Grants access to manage relationships.
     */
    public const val Follow: String = "follow"

    /**
     * Grants access to Web Push API subscriptions.
     */
    public const val Push: String = "push"

}