package me.taesu.kopringstandard.app.query

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
abstract class PageableCriteria(
    val page: Int = DEFAULT_PAGE,
    val size: Int = DEFAULT_SIZE,
) {
    open val pageRequest get() = PageRequest.of(this.page - 1, this.size)
    val offset get() = pageRequest.offset

    abstract val sort: List<Sortable>

    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_SIZE = 20
    }
}

interface Sortable {
    val columnName: String
    val direction: Sort.Direction
}

interface Filterable {
    @get:JsonIgnore
    val filterable: Boolean
}

