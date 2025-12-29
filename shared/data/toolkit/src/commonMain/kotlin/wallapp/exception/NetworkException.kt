package wallapp.exception

import wallapp.error.ErrorStatusCode

class NetworkException : RuntimeException {
    var errorStatusCode = ErrorStatusCode.Unknown
        private set

    constructor(message: String, cause: Throwable): super(message, cause)
    constructor(message: String): super(message)
    constructor(message: String, errorStatusCode: ErrorStatusCode): super(message) {
        this.errorStatusCode = errorStatusCode
    }
    constructor(cause: Throwable): super(cause)
    constructor(): super()
}