package wallapp.ads.reward.internal

object RewardAdInternalNavigatorNoOp : RewardAdInternalNavigator {
    override fun show() = false
    override fun hide() = false
}