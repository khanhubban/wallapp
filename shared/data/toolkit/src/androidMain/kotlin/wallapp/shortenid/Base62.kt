package wallapp.shortenid

import java.math.BigInteger
import java.util.Objects
import java.util.regex.Pattern

/**
 * Base62 encoder/decoder.
 *
 *
 * This is free and unencumbered public domain software
 *
 *
 * Source: https://github.com/opencoinage/opencoinage/blob/master/src/java/org/opencoinage/util/Base62.java
 */
internal object Base62 {
    private val BASE = BigInteger.valueOf(62)
    private const val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    /**
     * Encodes a number using Base62 encoding.
     *
     * @param number a positive integer
     * @return a Base62 string
     *
     * @throws IllegalArgumentException if `number` is a negative integer
     */
    fun encode(number: BigInteger): String {
        var number1 = number
        if (number1 < BigInteger.ZERO) {
            throwIllegalArgumentException("number must not be negative")
        }
        val result = StringBuilder()
        while (number1 > BigInteger.ZERO) {
            val divmod = number1.divideAndRemainder(BASE)
            number1 = divmod[0]
            val digit: Int = divmod[1].toInt()
            result.insert(0, DIGITS[digit])
        }
        return if (result.isEmpty()) DIGITS.substring(0, 1) else result.toString()
    }

    private fun throwIllegalArgumentException(format: String, vararg args: Any): BigInteger {
        throw IllegalArgumentException(String.format(format, *args))
    }

    /**
     * Decodes a string using Base62 encoding.
     *
     * @param string a Base62 string
     * @return a positive integer
     *
     * @throws IllegalArgumentException if `string` is empty
     */
    @JvmOverloads
    fun decode(string: String, bitLimit: Int = 128): BigInteger {
        Objects.requireNonNull(string, "Decoded string must not be null")
        if (string.isEmpty()) {
            return throwIllegalArgumentException("String '%s' must not be empty", string)
        }
        if (!Pattern.matches("[$DIGITS]*", string)) {
            throwIllegalArgumentException("String '%s' contains illegal characters, only '%s' are allowed", string, DIGITS)
        }
        return (string.indices)
            .map { index: Int -> BigInteger.valueOf(charAt(string, index).toLong()).multiply(BASE.pow(index)) }
            .reduce { acc, value ->
                val sum = acc.add(value)
                if (bitLimit > 0 && sum.bitLength() > bitLimit) {
                    throwIllegalArgumentException("String '%s' contains more than 128bit information", string)
                }
                sum
            }

    }

    private val charAt = { string: String, index: Int -> DIGITS.indexOf(string[string.length - index - 1]) }
}