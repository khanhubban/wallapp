package wallapp.pipeline.build

import java.security.MessageDigest

/** Stable positive Long id derived from a seed (e.g. "<remixId>:download"). */
fun mediaId(seed: String): Long {
    val d = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray())
    var v = 0L
    for (i in 0 until 7) v = (v shl 8) or (d[i].toLong() and 0xFF) // 56 bits → always positive
    return v
}

fun renditionUrl(baseUrl: String, path: String): String =
    baseUrl.trimEnd('/') + "/" + path.trimStart('/')
