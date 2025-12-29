package wallapp.ad

sealed interface AdSource {
    data object Random : AdSource

    data object AdNetwork : AdSource

    data object Preset : AdSource
}