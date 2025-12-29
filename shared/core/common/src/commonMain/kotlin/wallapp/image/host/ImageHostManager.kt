package wallapp.image.host

import wallapp.image.OnImageAction

interface ImageHostManager {

    fun getImageHostFormat(
        imageHostDisplayTarget: ImageHostDisplayTarget,
    ): ImageHostFormat

    fun createOnImageAction(
        imageHostDisplayTarget: ImageHostDisplayTarget,
        dynamicUrlResult: ImageHostDynamicUrlResult,
    ): OnImageAction?
}
