package wallapp.ads.inline

interface InlineAdInitFactory {

    fun createInlineAdHandle(
        inlineAdConfig: InlineAdConfig,
    ): InlineAdHandle

}