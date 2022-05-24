package me.taesu.kopringstandard.app.interfaces

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.domain.PageRequest

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
open class PageableCriteria(
    page: Int,
    size: Int,
    val showDeleted: Boolean,
) {
    val page = if (page < 1) 1 else page
    val size = if (size > 100) 100 else size

    @JsonIgnore
    val pageRequest = PageRequest.of(this.page - 1, this.size)
}