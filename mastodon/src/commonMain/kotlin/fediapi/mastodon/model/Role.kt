package fediapi.mastodon.model

import kotlinx.serialization.Serializable

/**
 * Represents a custom user role that grants permissions.
 *
 * @param id The ID of the [Role] in the database.
 * @param name The name of the role.
 * @param color The hex code assigned to this role. If no hex code is assigned, the string will be empty.
 * @param permissions All the permissions granted to the role.
 * @param highlighted Whether the role is publicly visible as a badge on user profiles.
 */
@Serializable
public data class Role(
    val id: Int,
    val name: String,
    val color: String,
    val permissions: Permissions,
    val highlighted: Boolean
)
