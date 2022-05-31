package me.taesu.kopringstandard.app.interfaces

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2022/05/27.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@RestController
@ControllerAdvice
class ExceptionHandlerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Map<String, String?> {
        return mapOf(
            "errorCode" to "INVALID_REQUEST",
            "message" to e.message,
        )
    }
}