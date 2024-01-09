package fediapi.mastodon.util

import io.ktor.client.request.forms.FormBuilder

internal fun FormBuilder.appendFieldsHash(
    fields: Map<String, String>,
    prefix: String = "fields_attributes"
) {
    if(fields.isEmpty()) {
        append("$prefix[0][name]", "")
        append("$prefix[0][value]", "")
    }
    fields.entries.forEachIndexed { i, field ->
        append("$prefix[$i][name]", field.key)
        append("$prefix[$i][value]", field.value)
    }
}