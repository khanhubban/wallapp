package wallapp.ads.inline.support



data class InlineAdItemFactories(val factories: List<InlineAdItemFactory>) {
    constructor(factory: InlineAdItemFactory) : this(listOf(factory))
}

