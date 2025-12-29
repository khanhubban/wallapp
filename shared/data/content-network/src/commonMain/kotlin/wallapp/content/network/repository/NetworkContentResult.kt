package wallapp.content.network.repository

import wallapp.content.network.model.NetworkContent

sealed interface NetworkContentResult {

    data class Success(val networkContent: NetworkContent) : NetworkContentResult

    data class Error(val errorMessage: String) : NetworkContentResult {
        init {
            println("Error: $errorMessage")
        }
    }
}

val NetworkContent.result: NetworkContentResult
    get() = NetworkContentResult.Success(this)