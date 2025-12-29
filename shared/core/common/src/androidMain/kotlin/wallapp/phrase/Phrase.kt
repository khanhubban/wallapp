/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wallapp.phrase

import android.content.Context
import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * A fluent API for formatting Strings. Canonical usage:
 * <pre>
 * CharSequence formatted = Phrase.from("Hi {first_name}, you are {age} years old.")
 * .put("first_name", firstName)
 * .put("age", age)
 * .format();
</pre> *
 *
 *  * Surround keys with curly braces; use two {{ to escape.
 *  * Keys start with lowercase letters followed by lowercase letters and underscores.
 *  * Spans are preserved, such as simple HTML tags found in strings.xml.
 *  * Fails fast on any mismatched keys.
 *
 * The constructor parses the original pattern into a doubly-linked list of [Token]s.
 * These tokens do not modify the original pattern, thus preserving any spans.
 *
 *
 * The [.format] method iterates over the tokens, replacing text as it iterates. The
 * doubly-linked list allows each token to ask its predecessor for the expanded length.
 */
class Phrase private constructor(pattern: CharSequence) {
    /** The unmodified original pattern.  */
    private val pattern: CharSequence

    /** All keys parsed from the original pattern, sans braces.  */
    private val keys: MutableSet<String> =
        HashSet()
    private val keysToValues: MutableMap<String, CharSequence> =
        HashMap()

    /** Cached result after replacing all keys with corresponding values.  */
    private var formatted: CharSequence? = null

    /** The constructor parses the original pattern into this doubly-linked list of tokens.  */
    private var head: Token? = null

    /** When parsing, this is the current character.  */
    private var curChar: Char
    private var curCharIndex = 0

    /**
     * Replaces the given key with a non-null value. You may reuse Phrase instances and replace
     * keys with new values.
     *
     * @throws IllegalArgumentException if the key is not in the pattern.
     */
    fun put(key: String, value: CharSequence?): Phrase {
        require(keys.contains(key)) { "Invalid key: $key" }
        requireNotNull(value) { "Null value for '$key'" }
        keysToValues[key] = value

        // Invalidate the cached formatted text.
        formatted = null
        return this
    }

    /**
     * Replaces the given key with the [Integer.toString] value for the given int.
     *
     * @see .put
     */
    fun put(key: String, value: Int): Phrase {
        return put(key, Integer.toString(value))
    }

    /**
     * Silently ignored if the key is not in the pattern.
     *
     * @see .put
     */
    fun putOptional(key: String, value: CharSequence?): Phrase {
        return if (keys.contains(key)) put(key, value) else this
    }

    /**
     * Replaces the given key, if it exists, with the [Integer.toString] value
     * for the given int.
     *
     * @see .putOptional
     */
    fun putOptional(key: String, value: Int): Phrase {
        return if (keys.contains(key)) put(key, value) else this
    }

    /**
     * Returns the text after replacing all keys with values.
     *
     * @throws IllegalArgumentException if any keys are not replaced.
     */
    fun format(): CharSequence {
        if (formatted == null) {
            if (!keysToValues.keys.containsAll(keys)) {
                val missingKeys: MutableSet<String> =
                    HashSet(keys)
                missingKeys.removeAll(keysToValues.keys)
                throw IllegalArgumentException("Missing keys: $missingKeys")
            }

            // Copy the original pattern to preserve all spans, such as bold, italic, etc.
            val sb = SpannableStringBuilder(pattern)
            var t = head
            while (t != null) {
                t.expand(sb, keysToValues)
                t = t.next
            }
            formatted = sb
        }
        return formatted!!
    }

    /** "Formats and sets as text in textView."  */
    fun into(textView: TextView?) {
        requireNotNull(textView) { "TextView must not be null." }
        textView.text = format()
    }

    /**
     * Returns the raw pattern without expanding keys; only useful for debugging. Does not pass
     * through to [.format] because doing so would drop all spans.
     */
    override fun toString(): String {
        return pattern.toString()
    }

    /** Returns the next token from the input pattern, or null when finished parsing.  */
    private fun token(prev: Token?): Token? {
        if (curChar.code == EOF) {
            return null
        }
        if (curChar == '{') {
            val nextChar = lookahead()
            return if (nextChar == '{') {
                leftCurlyBracket(prev)
            } else if (nextChar >= 'a' && nextChar <= 'z') {
                key(prev)
            } else {
                throw IllegalArgumentException(
                    "Unexpected first character '$nextChar'; must be lower case a-z."
                )
            }
        }
        return text(prev)
    }

    /** Parses a key: "{some_key}".  */
    private fun key(prev: Token?): KeyToken {

        // Store keys as normal Strings; we don't want keys to contain spans.
        val sb = StringBuilder()

        // Consume the opening '{'.
        consume()
        while (curChar >= 'a' && curChar <= 'z' || curChar == '_') {
            sb.append(curChar)
            consume()
        }

        // Consume the closing '}'.
        require(curChar == '}') {
            ("Unexpected character '" + curChar
                    + "'; expecting lower case a-z, '_', or '}'")
        }
        consume()

        // Disallow empty keys: {}.
        require(sb.length != 0) { "Empty key: {}" }
        val key = sb.toString()
        keys.add(key)
        return KeyToken(prev, key)
    }

    /** Consumes and returns a token for a sequence of text.  */
    private fun text(prev: Token?): TextToken {
        val startIndex = curCharIndex
        while (curChar != '{' && curChar.code != EOF) {
            consume()
        }
        return TextToken(prev, curCharIndex - startIndex)
    }

    /** Consumes and returns a token representing two consecutive curly brackets.  */
    private fun leftCurlyBracket(prev: Token?): LeftCurlyBracketToken {
        consume()
        consume()
        return LeftCurlyBracketToken(prev)
    }

    /** Returns the next character in the input pattern without advancing.  */
    private fun lookahead(): Char {
        return if (curCharIndex < pattern.length - 1) pattern[curCharIndex + 1] else EOF.toChar()
    }

    /**
     * Advances the current character position without any error checking. Consuming beyond the
     * end of the string can only happen if this parser contains a bug.
     */
    private fun consume() {
        curCharIndex++
        curChar =
            if (curCharIndex == pattern.length) EOF.toChar() else pattern[curCharIndex]
    }

    private abstract class Token protected constructor(private val prev: Token?) {
        var next: Token? = null

        /** Replace text in `target` with this token's associated value.  */
        abstract fun expand(
            target: SpannableStringBuilder,
            data: Map<String, CharSequence>
        )

        /** Returns the number of characters after expansion.  */
        abstract val formattedLength: Int

        /** Returns the character index after expansion.  */
        val formattedStart: Int
            get() = if (prev == null) {
                // The first token.
                0
            } else {
                // Recursively ask the predecessor node for the starting index.
                prev.formattedStart + prev.formattedLength
            }

        init {
            if (prev != null) prev.next = this
        }
    }

    /** Ordinary text between tokens.  */
    private class TextToken internal constructor(
        prev: Token?,
        override val formattedLength: Int
    ) : Token(prev) {
        override fun expand(
            target: SpannableStringBuilder,
            data: Map<String, CharSequence>
        ) {
            // Don't alter spans in the target.
        }

    }

    /** A sequence of two curly brackets.  */
    private class LeftCurlyBracketToken internal constructor(prev: Token?) :
        Token(prev) {
        override fun expand(
            target: SpannableStringBuilder,
            data: Map<String, CharSequence>
        ) {
            val start = formattedStart
            target.replace(start, start + 2, "{")
        }

        // Replace {{ with {.
        override val formattedLength: Int
            get() =// Replace {{ with {.
                1
    }

    private class KeyToken internal constructor(
        prev: Token?,
        /** The key without { and }.  */
        private val key: String
    ) : Token(prev) {
        private var value: CharSequence? = null
        override fun expand(
            target: SpannableStringBuilder,
            data: Map<String, CharSequence>
        ) {
            value = data[key]
            val replaceFrom = formattedStart
            // Add 2 to account for the opening and closing brackets.
            val replaceTo = replaceFrom + key.length + 2
            target.replace(replaceFrom, replaceTo, value)
        }

        // Note that value is only present after expand. Don't error check because this is all
        // private code.
        override val formattedLength: Int
            get() =// Note that value is only present after expand. Don't error check because this is all
                // private code.
                value!!.length

    }

    companion object {
        /** Indicates parsing is complete.  */
        private const val EOF = 0

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun from(v: View, @StringRes patternResourceId: Int): Phrase {
            return from(v.resources, patternResourceId)
        }

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun from(c: Context, @StringRes patternResourceId: Int): Phrase {
            return from(c.resources, patternResourceId)
        }

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun from(r: Resources, @StringRes patternResourceId: Int): Phrase {
            return from(r.getText(patternResourceId))
        }

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun fromPlural(
            v: View,
            @PluralsRes patternResourceId: Int,
            quantity: Int
        ): Phrase {
            return fromPlural(v.resources, patternResourceId, quantity)
        }

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun fromPlural(
            c: Context,
            @PluralsRes patternResourceId: Int,
            quantity: Int
        ): Phrase {
            return fromPlural(c.resources, patternResourceId, quantity)
        }

        /**
         * Entry point into this API.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        fun fromPlural(
            r: Resources,
            @PluralsRes patternResourceId: Int,
            quantity: Int
        ): Phrase {
            return from(r.getQuantityText(patternResourceId, quantity))
        }

        /**
         * Entry point into this API; pattern must be non-null.
         *
         * @throws IllegalArgumentException if pattern contains any syntax errors.
         */
        @JvmStatic
        fun from(pattern: CharSequence): Phrase {
            return Phrase(pattern)
        }
    }

    init {
        curChar = if (pattern.length > 0) pattern[0] else EOF.toChar()
        this.pattern = pattern

        // A hand-coded lexer based on the idioms in "Building Recognizers By Hand".
        // http://www.antlr2.org/book/byhand.pdf.
        var prev: Token? = null
        var next: Token?
        while (token(prev).also { next = it } != null) {
            // Creates a doubly-linked list of tokens starting with head.
            if (head == null) head = next
            prev = next
        }
    }
}