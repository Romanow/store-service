package ru.romanow.inst.services.common.config

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationPredicate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.observation.ServerRequestObservationContext

@Configuration
@ConditionalOnProperty("management.tracing.enabled", havingValue = "true")
class TracingConfiguration(
    private val serverProperties: ServerProperties
) {

    @Bean
    fun noActuatorObservations() = ObservationPredicate { name: String, context: Observation.Context? ->
        if (name == "http.server.requests" && context is ServerRequestObservationContext) {
            !context.carrier.servletPath.startsWith("${serverProperties.servlet.contextPath}/manage")
        } else {
            true
        }
    }

    @Bean
    fun noSecurityObservations() =
        ObservationPredicate { name: String, _: Observation.Context? -> !name.startsWith("spring.security.") }
}
