package com.example.booking.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream

fun String.joinPath(path: String): String {
    return when {
        this.endsWith("/") && path.startsWith("/") ->
            this + path.drop(1)

        !this.endsWith("/") && !path.startsWith("/") ->
            "$this/$path"

        else ->
            this + path
    }
}

fun uriToMultipart(
    context: Context,
    uri: Uri,
    partName: String,
    filename: String
): MultipartBody.Part? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        MultipartBody.Part.createFormData(partName, filename, requestBody)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}