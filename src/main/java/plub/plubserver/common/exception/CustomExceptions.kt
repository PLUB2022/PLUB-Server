package plub.plubserver.common.exception

open class PlubException : RuntimeException {
    @JvmField
    var statusCode: StatusCode

    constructor(statusCode: StatusCode) : super(statusCode.message) {
        this.statusCode = statusCode
    }

    constructor(statusCode: StatusCode, message: String?) : super(message) {
        this.statusCode = statusCode
    }
}

class ArchiveException(statusCode: StatusCode?) : PlubException(statusCode!!)

