package io.renegadelabs.canary.api.shared.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Order(-2)
@Component
class WebExceptionHandler(
    errorAttributes: ErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer,
    @Value("\${spring.webflux.base-path}") private val contextPath: String
) : AbstractErrorWebExceptionHandler(errorAttributes, WebProperties.Resources(), applicationContext) {
    
    init {
        this.setMessageReaders(serverCodecConfigurer.readers)
        this.setMessageWriters(serverCodecConfigurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { r -> this.renderErrorResponse(r) }
    }

    private fun renderErrorResponse(serverRequest: ServerRequest): Mono<ServerResponse> {
        val errorAttributes = super.getErrorAttributes(
            serverRequest,
            ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
            )
        )

        val responseStatus = HttpStatus.valueOf(errorAttributes["status"] as Int)

        val responseMap = mapOf(
            "timestamp" to errorAttributes["timestamp"],
            "path" to "${this.contextPath}${errorAttributes["path"]}",
            "status" to responseStatus.value(),
            "error" to errorAttributes["error"],
            "requestId" to errorAttributes["requestId"]
        )

        return ServerResponse.status(responseStatus)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(responseMap))
    }
}
