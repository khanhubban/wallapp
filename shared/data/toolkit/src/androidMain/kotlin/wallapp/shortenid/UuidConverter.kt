package wallapp.shortenid

import java.math.BigInteger
import java.util.UUID

internal object UuidConverter {
    fun toBigInteger(uuid: UUID): BigInteger {
        return BigIntegerPairing.pair(
            BigInteger.valueOf(uuid.mostSignificantBits),
            BigInteger.valueOf(uuid.leastSignificantBits)
        )
    }

    fun toUuid(value: BigInteger): UUID {
        val unpaired = BigIntegerPairing.unpair(value)
        return UUID(unpaired[0].toLong(), unpaired[1].toLong())
    }
}