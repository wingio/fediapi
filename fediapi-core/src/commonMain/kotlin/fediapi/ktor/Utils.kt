package fediapi.ktor

import fediapi.http.paging.PageInfo
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * Shorthand for setting a MultiPart Form Data request body
 */
public fun HttpRequestBuilder.setForm(formBuilder: FormBuilder.() -> Unit) {
    setBody(
        MultiPartFormDataContent(
            formData(formBuilder)
        )
    )
}

public fun HttpRequestBuilder.setFormField(key: String, value: String) {
    setForm {
        append(key, value)
    }
}

public fun FormBuilder.appendFile(
    key: String,
    bytes: ByteArray,
    fileName: String = key
) {
    append(
        key = key,
        value = bytes,
        headers = Headers.build {
            append(
                HttpHeaders.ContentDisposition,
                "filename=$fileName"
            )
        }
    )
}