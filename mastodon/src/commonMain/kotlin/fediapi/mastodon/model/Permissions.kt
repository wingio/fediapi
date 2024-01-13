package fediapi.mastodon.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Permissions assigned to a given [Role]
 *
 * @see Permissions.toList
 *
 * @param bits The raw bitmask containing the permissions
 */
@Serializable(with = Permissions.Serializer::class)
public class Permissions(
    public val bits: Int
) {

    /**
     * Represents a permission that can be granted to a role
     */
    public enum class Permission(
        public val mask: Int
    ) {
        /**
         * Users with this permission bypass all permissions.
         */
        ADMINISTRATOR(0x1),

        /**
         * Allows users to access Sidekiq and PgHero dashboards.
         */
        DEVOPS(0x2),

        /**
         * Allows users to see history of admin actions.
         */
        VIEW_AUDIT_LOG(0x4),

        /**
         * Allows users to access the dashboard and various metrics.
         */
        VIEW_DASHBOARD(0x8),

        /**
         * Allows users to review reports and perform moderation actions against them.
         */
        MANAGE_REPORTS(0x10),

        /**
         * Allows users to block or allow federation with other domains, and control deliverability.
         */
        MANAGE_FEDERATION(0x20),

        /**
         * Allows users to change site settings.
         */
        MANAGE_SETTINGS(0x40),

        /**
         * Allows users to block e-mail providers and IP addresses.
         */
        MANAGE_BLOCKS( 0x80),

        /**
         * Allows users to review trending content and update hashtag settings.
         */
        MANAGE_TAXONOMIES( 0x100),

        /**
         * Allows users to review appeals against moderation actions.
         */
        MANAGE_APPEALS( 0x200),

        /**
         * Allows users to view other users’ details and perform moderation actions against them.
         */
        MANAGE_USERS( 0x400),

        /**
         * Allows users to browse and deactivate invite links.
         */
        MANAGE_INVITES( 0x800),

        /**
         * Allows users to change server rules.
         */
        MANAGE_RULES(0x1000),

        /**
         * Allows users to manage announcements on the server.
         */
        MANAGE_ANNOUNCEMENTS(0x2000),

        /**
         * Allows users to manage custom emojis on the server.
         */
        MANAGE_CUSTOM_EMOJIS(0x4000),

        /**
         * Allows users to set up webhooks for administrative events.
         */
        MANAGE_WEBHOOKS(0x8000),

        /**
         * Allows users to invite new people to the server.
         */
        INVITE_USERS(0x10000),

        /**
         * Allows users to manage and assign roles below theirs.
         */
        MANAGE_ROLES(0x20000),

        /**
         * Allows users to disable other users’ two-factor authentication, change their e-mail address, and reset their password.
         */
        MANAGE_USER_ACCESS(0x40000),

        /**
         * Allows users to delete other users’ data without delay.
         */
        DELETE_USER_DATA(0x80000)
    }

    /**
     * Users with this permission bypass all permissions.
     */
    public var administrator: Boolean = hasPermission(Permission.ADMINISTRATOR)

    /**
     * Allows users to access Sidekiq and PgHero dashboards.
     */
    public var devops: Boolean = hasPermission(Permission.DEVOPS)

    /**
     * Allows users to see history of admin actions.
     */
    public var viewAuditLog: Boolean = hasPermission(Permission.VIEW_AUDIT_LOG)

    /**
     * Allows users to access the dashboard and various metrics.
     */
    public var viewDashboard: Boolean = hasPermission(Permission.VIEW_DASHBOARD)

    /**
     * Allows users to review reports and perform moderation actions against them.
     */
    public var manageReports: Boolean = hasPermission(Permission.MANAGE_REPORTS)

    /**
     * Allows users to block or allow federation with other domains, and control deliverability.
     */
    public var manageFederation: Boolean = hasPermission(Permission.MANAGE_FEDERATION)

    /**
     * Allows users to change site settings.
     */
    public var manageSettings: Boolean = hasPermission(Permission.MANAGE_SETTINGS)

    /**
     * Allows users to block e-mail providers and IP addresses.
     */
    public var manageBlocks: Boolean = hasPermission(Permission.MANAGE_BLOCKS)

    /**
     * Allows users to review trending content and update hashtag settings.
     */
    public var manageTaxonomies: Boolean = hasPermission(Permission.MANAGE_TAXONOMIES)

    /**
     * Allows users to review appeals against moderation actions.
     */
    public var manageAppeals: Boolean = hasPermission(Permission.MANAGE_APPEALS)

    /**
     * Allows users to view other users’ details and perform moderation actions against them.
     */
    public var manageUsers: Boolean = hasPermission(Permission.MANAGE_USERS)

    /**
     * Allows users to browse and deactivate invite links.
     */
    public var manageInvites: Boolean = hasPermission(Permission.MANAGE_INVITES)

    /**
     * Allows users to change server rules.
     */
    public var manageRules: Boolean = hasPermission(Permission.MANAGE_RULES)

    /**
     * Allows users to manage announcements on the server.
     */
    public var manageAnnouncements: Boolean = hasPermission(Permission.MANAGE_ANNOUNCEMENTS)

    /**
     * Allows users to manage custom emojis on the server.
     */
    public var manageCustomEmojis: Boolean = hasPermission(Permission.MANAGE_CUSTOM_EMOJIS)

    /**
     * Allows users to set up webhooks for administrative events.
     */
    public var manageWebhooks: Boolean = hasPermission(Permission.MANAGE_WEBHOOKS)

    /**
     * Allows users to invite new people to the server.
     */
    public var inviteUsers: Boolean = hasPermission(Permission.INVITE_USERS)

    /**
     * Allows users to manage and assign roles below theirs.
     */
    public var manageRoles: Boolean = hasPermission(Permission.MANAGE_ROLES)

    /**
     * Allows users to disable other users’ two-factor authentication, change their e-mail address, and reset their password.
     */
    public var manageUserAccess: Boolean = hasPermission(Permission.MANAGE_USER_ACCESS)

    /**
     * Allows users to delete other users’ data without delay.
     */
    public var deleteUserData: Boolean = hasPermission(Permission.DELETE_USER_DATA)

    /**
     * Checks if the [permission] is included in this set
     */
    public fun hasPermission(permission: Permission): Boolean {
        return (bits and permission.mask) != 0
    }

    /**
     * Converts this set of permissions into an easily workable list
     */
    public fun toList(): List<Permission> {
        if (bits == 0) return emptyList() // Don't waste time looping through permissions if none are present
        val perms = mutableListOf<Permission>()
        Permission.entries.forEach { permission ->
            if (hasPermission(permission)) perms.add(permission)
        }
        return perms
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Permissions) return false
        return bits == other.bits
    }

    override fun hashCode(): Int {
        return bits
    }

    public companion object {

        /**
         * Creates a [Permissions] object using the provided [permissions]
         */
        public fun fromList(permissions: List<Permission>): Permissions {
            var bits = 0
            permissions.forEach { permission ->
                bits = bits or permission.mask
            }
            return Permissions(bits)
        }

        /**
         * Creates a [Permissions] object from the provided [permissions][permission]
         */
        public fun from(vararg permission: Permission): Permissions = fromList(permission.toList())

    }

    internal object Serializer: KSerializer<Permissions> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Permissions", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Permissions {
            return Permissions(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: Permissions) {
            encoder.encodeInt(value.bits)
        }

    }

}