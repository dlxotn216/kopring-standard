package me.taesu.kopringstandard.app.interfaces

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import me.taesu.kopringstandard.app.context.HttpRequestContext
import me.taesu.kopringstandard.app.exception.*
import me.taesu.kopringstandard.app.vo.SimpleMessage
import org.springframework.beans.BeanInstantiationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by itaesu on 2022/05/27.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@RestController
@RestControllerAdvice
class ExceptionHandlerAdvice
// : ResponseEntityExceptionHandler()
{
    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(
        e: InvalidRequestException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        return toFailResponse(e)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        return when (val cause = e.cause) {
            is MissingKotlinParameterException -> toFailResponse(cause)

            is JsonMappingException -> toFailResponse(cause)

            else -> ErrorCode.INVALID_REQUEST.run {
                FailResponse(
                    errorCode = this,
                    errorMessage = SimpleMessage(this.messageId),
                    debugMessage = e.resolveMessage(),
                )
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidFormatException(e: InvalidFormatException, requestContext: HttpRequestContext): FailResponse {
        val fieldName = e.path.firstOrNull()?.fieldName ?: ""
        val errorCode = ErrorCode.INVALID_REQUEST
        return toFieldError(
            errorCode = errorCode,
            fieldName = fieldName,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(errorCode.messageId),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingKotlinParameterException::class)
    fun handleMissingKotlinParameterException(
        e: MissingKotlinParameterException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        val fieldName = e.parameter.name ?: ""
        val errorCode = ErrorCode.REQUIRED_PARAMETER
        return toFieldError(
            errorCode = errorCode,
            fieldName = fieldName,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(errorCode.messageId),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        return handleBeanBindingException(e, requestContext)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException::class)
    fun handleBeanBindingException(
        e: BindException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        val fieldName = e.bindingResult.fieldError?.field ?: ""
        val errorCode = ErrorCode.INVALID_PARAMETER
        val messageId = e.bindingResult.fieldError?.defaultMessage ?: errorCode.messageId
        return toFieldError(
            errorCode = errorCode,
            fieldName = fieldName,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(messageId),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        e: DataIntegrityViolationException,
        request: HttpServletRequest,
        response: HttpServletResponse,
        requestContext: HttpRequestContext,
    ): FailResponse {
        if (e.rootCause?.message?.contains("unique constraint") != true) {
            return handleException(e, request, response, requestContext)
        }

        val errorCode = ErrorCode.UNEXPECTED_DATA_ALREADY_EXISTS
        return toFieldError(
            errorCode = errorCode,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(errorCode.messageId),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        val errorCode = ErrorCode.REQUIRED_PARAMETER
        return toFieldError(
            errorCode = errorCode,
            fieldName = e.parameterName,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(errorCode.messageId),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(
        e: MaxUploadSizeExceededException,
        requestContext: HttpRequestContext,
    ): FailResponse {
        val errorCode = ErrorCode.MAXIMUM_FILE_SIZE_EXCEEDED
        return toFieldError(
            errorCode = errorCode,
            fieldName = "file",
            message = SimpleMessage(errorCode.messageId),
            debugMessage = e.resolveMessage(),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BeanInstantiationException::class)
    fun handleBeanInstantiationException(
        e: BeanInstantiationException,
        response: HttpServletResponse,
        requestContext: HttpRequestContext,
    ): FailResponse {
        val errorCode = ErrorCode.INVALID_PARAMETER
        val message = e.cause?.message ?: errorCode.messageId
        return toFieldError(
            errorCode = errorCode,
            debugMessage = e.resolveMessage(),
            message = SimpleMessage(message),
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnexpectedStatusException::class)
    fun handleUnexpectedStatusException(
        e: UnexpectedStatusException,
        response: HttpServletResponse,
        requestContext: HttpRequestContext,
    ): FailResponse {
        response.addHeader("X-EXCEPTION-LEVEL", ExceptionLevel.FATAL.name)
        val errorCode = e.errorCode
        return FailResponse(
            errorCode = errorCode,
            errorMessage = SimpleMessage(errorCode.messageId),
            debugMessage = "${e.resolveMessage()}, cause: [${e.cause?.resolveMessage() ?: ""}]",
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse,
        requestContext: HttpRequestContext,
    ): FailResponse {
        response.addHeader("X-EXCEPTION-LEVEL", ExceptionLevel.FATAL.name)
        val errorCode = ErrorCode.UNEXPECTED
        return FailResponse(
            errorCode = errorCode,
            errorMessage = SimpleMessage(errorCode.messageId),
            debugMessage = e.resolveMessage(),
        )
    }
}
