/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.web

import io.swagger.v3.oas.annotations.Hidden
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.romanow.services.store.model.ErrorResponse

@Hidden
@RestControllerAdvice(annotations = [RestController::class])
class ExceptionController {
    private val logger = LoggerFactory.getLogger(ExceptionController::class.java)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun badRequest(exception: MethodArgumentNotValidException): ErrorResponse {
        val validationErrors = prepareValidationErrors(exception.bindingResult.fieldErrors)
        logger.debug("Bad Request: {}", validationErrors)
        return ErrorResponse(validationErrors)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun notFound(exception: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message!!)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException::class)
    fun handleException(exception: RuntimeException): ErrorResponse {
        logger.error("", exception)
        return ErrorResponse(exception.message)
    }

    private fun prepareValidationErrors(errors: List<FieldError>): String {
        return errors.joinToString(";") { "Field " + it.field + " has wrong value: [" + it.defaultMessage + "]" }
    }
}
