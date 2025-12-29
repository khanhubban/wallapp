package wallapp.image.host


object ImageHostUrlUnmapper {

    /**
     * Extracts the "expires" query parameter from the given URL and returns it as a Long.
     * This is a helper function used for debugging purposes.
     */
    fun extractExpiresEpochFromUrl(url: String): Long? {
        val queryStartIndex = url.indexOf("?")
        if (queryStartIndex == -1) return null

        val queryString = url.substring(queryStartIndex + 1)
        val parameters = queryString.split("&")

        for (parameter in parameters) {
            val keyValuePair = parameter.split("=")
            if (keyValuePair.size == 2 && keyValuePair[0] == "expires") {
                return keyValuePair[1].toLong()
            }
        }

        return null
    }

}