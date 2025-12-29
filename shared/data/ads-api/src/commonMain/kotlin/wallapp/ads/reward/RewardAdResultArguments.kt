package wallapp.ads.reward

class RewardAdResultArguments {

    companion object {
        const val REWARD_AD_RESULT_REQUEST_KEY = "rewardAdResult"

        private const val REWARD_AD_RESULT_DATA_ERROR_KEY = "error"

//        fun Fragment.setRewardAdFragmentResult(error: Boolean) {
//            Log.d("[RewardAdDebug] result, error: $error")
//            activity?.supportFragmentManager?.setFragmentResult(
//                REWARD_AD_RESULT_REQUEST_KEY,
//                bundleOf(REWARD_AD_RESULT_DATA_ERROR_KEY to error),
//            )
//        }

//        fun Fragment.setRewardAdFragmentResultListener(
//            navigationActions: NavigationActions,
//        ) {
//            Log.d("[RewardAdDebug] setFragmentResultListener()")
//            activity?.supportFragmentManager?.setFragmentResultListener(
//                REWARD_AD_RESULT_REQUEST_KEY,
//                this,
//            ) { requestKey, bundle ->
//                Log.d("[RewardAdDebug] onResult($requestKey)")
//                if (requestKey == REWARD_AD_RESULT_REQUEST_KEY) {
//                    if (bundle.getBoolean(REWARD_AD_RESULT_DATA_ERROR_KEY)) {
//                        navigationActions.toRewardAdError(
//                            RewardAdErrorArguments(RewardAdErrorType.General)
//                        ).navigate(findNavController())
//                    }
//                }
//            }
//        }
    }

}