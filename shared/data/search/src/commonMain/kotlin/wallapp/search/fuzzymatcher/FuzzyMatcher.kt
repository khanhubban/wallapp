package wallapp.search.fuzzymatcher

object FuzzyMatcher {

    // https://en.wikipedia.org/wiki/Levenshtein_distance
    fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) { 0 } }

        for (i in 0..m) {
            for (j in 0..n) {
                when {
                    i == 0 -> dp[i][j] = j
                    j == 0 -> dp[i][j] = i
                    else -> {
                        dp[i][j] = when {
                            s1[i - 1] == s2[j - 1] -> dp[i - 1][j - 1]
                            else -> 1 + minOf(dp[i][j - 1], dp[i - 1][j], dp[i - 1][j - 1])
                        }
                    }
                }
            }
        }
        return dp[m][n]
    }

    fun fuzzyMatch(term: String, options: List<String>, threshold: Int): List<String> {
        return options.filter { levenshteinDistance(term, it) <= threshold }
    }
}
