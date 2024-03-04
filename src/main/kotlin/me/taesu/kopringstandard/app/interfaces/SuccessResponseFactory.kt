package me.taesu.kopringstandard.app.interfaces

import me.taesu.kopringstandard.app.query.PageableCriteria
import me.taesu.kopringstandard.app.query.PaginatedRow
import me.taesu.kopringstandard.app.vo.SimpleText
import me.taesu.kopringstandard.app.vo.Translatable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class SuccessResponseFactory private constructor() {
    companion object {
        fun <T: Any?> of(
            result: T,
            message: Translatable = SimpleText("Request was success."),
        ): SuccessResponse<T> {
            return SuccessResponse(result = result, message = message)
        }

        fun <T: PaginatedRow> of(
            pageResult: Page<T>,
            parameters: Map<String, String>,
            criteria: PageableCriteria,
            message: Translatable = SimpleText("Request was success."),
            additional: Map<String, Any> = emptyMap(),
        ): SuccessResponse<PaginatedResult> {
            val lastRowNumber = if (pageResult.isEmpty) {
                0
            } else {
                pageResult.lastRowNumber()
            }

            val content = pageResult.content.apply { fillNumber(this, lastRowNumber) }
            return SuccessResponse(
                result = PaginatedResult(
                    contents = content,
                    totalPages = pageResult.totalPages,
                    totalCount = pageResult.totalElements,
                    isFirst = pageResult.isFirst,
                    isLast = pageResult.isLast,
                    page = criteria.page,
                    size = criteria.size,
                    isEmpty = pageResult.isEmpty,
                    isFiltered = parameters.isNotEmpty(),
                    filteredElements = criteria,
                    additional = additional,
                ),
                message = message,
            )
        }

        fun <T: PaginatedRow> fillNumber(
            content: List<T>,
            lastRowNumber: Long = content.size.toLong(),
            direction: Sort.Direction = Sort.Direction.DESC,
        ) {
            when (direction) {
                Sort.Direction.ASC -> {
                    var no = 1L
                    content.forEach { it.no = no++ }
                }

                Sort.Direction.DESC -> {
                    var no = lastRowNumber
                    content.forEach { it.no = no-- }
                }
            }
        }
    }
}

fun <T> Page<T>.lastRowNumber() = this.totalElements - (this.pageable.pageNumber * this.pageable.pageSize)

