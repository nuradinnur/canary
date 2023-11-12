package io.renegadelabs.canary.api.shared.component

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Order(-2)
@Component
class WebExceptionHandler(
    private val errorAttributes: ErrorAttributes,
    private val applicationContext: ApplicationContext,
    private val serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, WebProperties.Resources(), applicationContext) {

    init {
        this.setMessageReaders(this.serverCodecConfigurer.readers)
        this.setMessageWriters(this.serverCodecConfigurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { r -> this.renderErrorResponse(r) }
    }

    private fun renderErrorResponse(serverRequest: ServerRequest): Mono<ServerResponse> {
        val errorAttributes = this.errorAttributes.getErrorAttributes(
            serverRequest,
            ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
            )
        )
        val responseStatus = errorAttributes["status"] as HttpStatus
        val responseMap = mapOf(
            "timestamp" to errorAttributes["timestamp"],
            "path" to serverRequest.exchange().request.uri.path,
            "status" to responseStatus.value(),
            "message" to errorAttributes["message"],
            "request_id" to errorAttributes["requestId"]
        )
        return ServerResponse.status(responseStatus).body(BodyInserters.fromValue(responseMap))
    }

    override fun logError(serverRequest: ServerRequest, serverResponse: ServerResponse, throwable: Throwable) {
        println("serverRequest.exchange().request.uri.path == ...")
        println(serverRequest.exchange().request.uri.path)
        // TODO: remove throwable stacktraces from logging in non-development profiles
        val responseStatus = HttpStatus.resolve(serverResponse.statusCode().value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
        if (logger.isErrorEnabled && responseStatus.is5xxServerError)
            logger.error(throwable) { this.formatError(serverRequest, responseStatus) }
        else if (logger.isInfoEnabled && !responseStatus.is5xxServerError)
            logger.info(throwable) { this.formatError(serverRequest, responseStatus) }
    }

    private fun formatError(serverRequest: ServerRequest, httpStatus: HttpStatus): String {
        return "${serverRequest.exchange().logPrefix.trim()} ${serverRequest.method()} " +
                "\"${serverRequest.uri().path}\" returned HTTP ${httpStatus.value()} - ${httpStatus.reasonPhrase}"
    }
}
