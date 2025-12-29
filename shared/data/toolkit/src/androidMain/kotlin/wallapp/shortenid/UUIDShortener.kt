package wallapp.shortenid

import java.util.UUID

/**
 * Create Shortened Id
 *
 * @return Shortened Id encoded UUID
 */
fun createShortenedId(): String {
    return encode(UUID.randomUUID())
}

/**
 * Encode UUID to FriendlyId id
 *
 * @param uuid UUID to be encoded
 * @return Shortened Id encoded UUID
 */
fun toShortenedId(uuid: UUID): String {
    return encode(uuid)
}

/**
 * Decode Shortened Id to UUID
 *
 * @param friendlyId encoded UUID
 * @return decoded UUID
 */
fun toUuid(friendlyId: String): UUID {
    return decode(friendlyId)
}