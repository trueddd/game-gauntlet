package com.github.trueddd.util

import io.ktor.http.*
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

fun ByteArray.toBlob(): Blob {
    val array = Uint8Array(size)
    forEachIndexed { index, byte ->
        array[index] = byte
    }
    return Blob(
        JsArray<JsAny?>().apply { set(0, array.buffer) },
        BlobPropertyBag(ContentType.Application.OctetStream.toString())
    )
}
