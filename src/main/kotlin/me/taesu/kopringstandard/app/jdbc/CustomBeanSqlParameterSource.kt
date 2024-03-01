package me.taesu.kopringstandard.app.jdbc

import org.springframework.beans.PropertyAccessorFactory
import org.springframework.jdbc.core.StatementCreatorUtils
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class CustomBeanPropertySqlParameterSource(parameter: Any): BeanPropertySqlParameterSource(parameter) {
}
