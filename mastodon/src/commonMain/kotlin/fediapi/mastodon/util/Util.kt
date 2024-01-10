package fediapi.mastodon.util

import fediapi.http.paging.PageInfo
import io.ktor.client.request.*
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

/**
 * Applies the correct query parameters from [pageInfo]
 */
public fun HttpRequestBuilder.addPageParams(
    pageInfo: PageInfo?
) {
    pageInfo?.let {
        parameter("max_id", pageInfo.max)
        parameter("min_id", pageInfo.min)
        parameter("since_id", pageInfo.since)
    }
}