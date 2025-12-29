package wallapp.ads.appopen

data class AppOpenAdUnitId(val id: String) {

    init {
        require(id.isNotEmpty())
    }

}