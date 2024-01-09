package fediapi.mastodon.model.request

public enum class GrantType(public val value: String) {
    CODE("authorization_code"),
    APP("client_credentials")
}