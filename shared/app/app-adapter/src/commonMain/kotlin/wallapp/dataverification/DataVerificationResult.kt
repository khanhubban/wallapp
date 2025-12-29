package wallapp.dataverification

import wallapp.content.model.WallpaperId
import wallapp.string.quote

sealed class DataVerificationResult {

    data class Success(val message: String) : DataVerificationResult() {

        constructor(wallpaperId: WallpaperId, message: String) : this("${wallpaperId.name.quote()} $message")
    }

    data class Error(val message: String) : DataVerificationResult() {

        constructor(wallpaperId: WallpaperId, message: String) : this("${wallpaperId.name.quote()} $message")
    }
}