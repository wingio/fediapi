package fediapi.mastodon.model.request

public enum class Privacy(public val value: String) {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private")
}