package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.JwtException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException

@Component
class WebFluxErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, options)
        val throwable = ExceptionUtils.getRootCause(super.getError(request))
        val errorStatus = this.determineHttpStatus(getError(request))
        errorAttributes["status"] = errorStatus
        errorAttributes["error"] = ResponseStatusException(errorStatus, throwable.localizedMessage)
        errorAttributes["message"] = throwable.localizedMessage
        return errorAttributes
    }

    private fun determineHttpStatus(error: Throwable): HttpStatus {
        // TODO: add all throwable exceptions here (is this good practice?)
        return when (error) {
            is BadCredentialsException -> HttpStatus.BAD_REQUEST
            is AuthenticationException, is JwtException -> HttpStatus.UNAUTHORIZED
            is ResponseStatusException ->
                HttpStatus.resolve(error.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            else ->
                HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}