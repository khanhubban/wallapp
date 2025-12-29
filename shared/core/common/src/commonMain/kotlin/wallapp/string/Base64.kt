package wallapp.string

import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8

object Base64 {

    fun encodeToBase64(input: String): String {
        return input.encodeUtf8().base64()
    }

    fun decodeFromBase64(base64String: String): String? {
        return base64String.decodeBase64()?.utf8()
    }
}