package wallapp.ads.inline.support

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.ads.AdType

class FullScreenAdShowListenerNoOp(adType: AdType = AdType.Reward) : FullScreenAdShowListener {

    override val adShowed: MutableStateFlow<AdType> = MutableStateFlow(adType)

    override val adDismissed: MutableStateFlow<AdType> = MutableStateFlow(adType)
}