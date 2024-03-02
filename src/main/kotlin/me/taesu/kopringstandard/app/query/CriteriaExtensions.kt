package me.taesu.kopringstandard.app.query

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */

inline fun <T: Filterable> T?.queryIf(block: (T) -> String): String? {
    if (this == null) return null
    if (!this.filterable) return null
    return block(this)
}

inline fun <T> T?.queryIf(block: (T) -> String): String? {
    if (this == null) return null
    return block(this)
}
