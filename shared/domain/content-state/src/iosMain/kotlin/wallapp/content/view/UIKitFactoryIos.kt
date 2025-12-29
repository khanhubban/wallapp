package wallapp.content.view

import platform.UIKit.UIView
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.image.ImageVideoState
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.UIKitFactory

interface UIKitFactoryIos : UIKitFactory {
    fun createProfileImage(profileViewState: ProfileImageViewState, imageSize: DpOptional? = null): UIView

    fun updateProfileImage(profileViewState: ProfileImageViewState, imageSize: DpOptional? = null)

    fun releaseProfileImage(profileViewState: ProfileImageViewState)

    fun createVideoPlayer(url: String? = null, fileName: String? = null, imageVideoState: ImageVideoState): UIView

    fun updateVideoPlayer(url: String? = null, fileName: String? = null, imageVideoState: ImageVideoState)

    fun releaseVideoPlayer(url: String? = null, fileName: String? = null, imageVideoState: ImageVideoState)
}