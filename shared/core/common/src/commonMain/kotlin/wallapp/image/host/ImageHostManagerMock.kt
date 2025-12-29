package wallapp.image.host

import wallapp.image.OnImageAction

class ImageHostManagerMock: ImageHostManager {

    override fun getImageHostFormat(
        imageHostDisplayTarget: ImageHostDisplayTarget,
    ): ImageHostFormat = ImageHostFormat.Png

    override fun createOnImageAction(
        imageHostDisplayTarget: ImageHostDisplayTarget,
        dynamicUrlResult: ImageHostDynamicUrlResult,
    ): OnImageAction? = null
}