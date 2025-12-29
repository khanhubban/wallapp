package wallapp.shortenid

import java.util.UUID

/**
 * Encode UUID to Url62 id
 *
 * @param uuid UUID to be encoded
 * @return url62 encoded UUID
 */
internal fun encode(uuid: UUID): String {
    val pair = UuidConverter.toBigInteger(uuid)
    return Base62.encode(pair)
}

/**
 * Decode url62 id to UUID
 *
 * @param id url62 encoded id
 * @return decoded UUID
 */
internal fun decode(id: String): UUID {
    val decoded = Base62.decode(id)
    return UuidConverter.toUuid(decoded)
}