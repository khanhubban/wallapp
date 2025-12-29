package wallapp.shortenid

import java.math.BigInteger

/**
 * Basing on snippet published by drmalex07
 *
 *
 * https://gist.github.com/drmalex07/9008c611ffde6cb2ef3a2db8668bc251
 */
internal object BigIntegerPairing {
    private val HALF = BigInteger.ONE.shiftLeft(64) // 2^64
    private val MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE)
    private val toUnsigned = { value: BigInteger -> if (value.signum() < 0) value.add(HALF) else value }
    private val toSigned = { value: BigInteger -> if (MAX_LONG.compareTo(value) < 0) value.subtract(HALF) else value }
    fun pair(hi: BigInteger, lo: BigInteger): BigInteger {
        val unsignedLo = toUnsigned(lo)
        val unsignedHi = toUnsigned(hi)
        return unsignedLo.add(unsignedHi.multiply(HALF))
    }

    fun unpair(value: BigInteger): Array<BigInteger> {
        val parts = value.divideAndRemainder(HALF)
        val signedHi = toSigned(parts[0])
        val signedLo = toSigned(parts[1])
        return arrayOf(signedHi, signedLo)
    }
}