package ru.romanow.inst.services.common.config

import io.micrometer.tracing.exporter.SpanExportingPredicate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("management.tracing.enabled", havingValue = "true")
class TracingConfiguration {

    @Bean
    fun noActuator() = SpanExportingPredicate { it.tags["uri"] == null || !it.tags["uri"]!!.startsWith("/manage") }
}
