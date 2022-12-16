package io.renegadelabs.canary.api.shared.component

import io.jsonwebtoken.JwtException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException

@Component
class WebFluxErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, options)
        val throwable = super.getError(request)
        val errorStatus = this.determineHttpStatus(getError(request))

        errorAttributes["error"] = ResponseStatusException(errorStatus, throwable.localizedMessage)
        errorAttributes["error"] = throwable.localizedMessage
        errorAttributes["status"] = errorStatus
        return errorAttributes
    }

    private fun determineHttpStatus(error: Throwable): HttpStatus {
        return when (error) {
            is AuthenticationException,
            is JwtException ->
                HttpStatus.UNAUTHORIZED
            is ResponseStatusException ->
                HttpStatus.resolve(error.statusCode.value()).let { HttpStatus.INTERNAL_SERVER_ERROR }
            else ->
                HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}