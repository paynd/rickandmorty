package se.ox.assigment.network.errors

sealed class CharacterError : Exception() {
    data object NetworkError : CharacterError()
    data object ApiError : CharacterError()
    data class HttpError(val code: Int) : CharacterError()
    data class Unknown(override val cause: Throwable?) : CharacterError()

    override val message: String
        get() = when (this) {
            is NetworkError -> "Network connection issue"
            is ApiError -> "Server response issue"
            is HttpError -> "HTTP error: $code"
            is Unknown -> "Unknown error: ${cause?.message}"
        }
}