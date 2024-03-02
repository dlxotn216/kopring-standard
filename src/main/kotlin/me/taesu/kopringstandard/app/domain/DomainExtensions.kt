package me.taesu.kopringstandard.app.domain

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
fun hash(vararg fields: Any?): Int {
    return fields.fold(0) { acc, any ->
        (acc * 31) + any.hashCode()
    }
}
