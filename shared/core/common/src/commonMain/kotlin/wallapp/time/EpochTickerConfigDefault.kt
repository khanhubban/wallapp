package wallapp.time

import kotlinx.coroutines.flow.Flow
import wallapp.appvisibility.AppVisibility

class EpochTickerConfigDefault(
    private val appVisibility: AppVisibility,
) : EpochTickerConfig {

    override val canIncrement: Flow<Boolean>
        get() = appVisibility.isVisible
}