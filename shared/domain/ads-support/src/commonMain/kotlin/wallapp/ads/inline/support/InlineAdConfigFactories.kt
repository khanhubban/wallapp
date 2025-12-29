package wallapp.ads.inline.support

data class InlineAdConfigFactories(val factories: List<InlineAdConfigFactory>) {
    constructor(factory: InlineAdConfigFactory) : this(listOf(factory))
}
