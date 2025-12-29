package wallapp.ads.inline

data class InlineAdUnitId(val id: String) {
    init {
        require(id.isNotEmpty())
    }
}