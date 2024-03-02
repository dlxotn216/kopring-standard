package me.taesu.kopringstandard.app.query

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Like 쿼리를 수행하기 위한 Filter VO
 * Kotlin 환경에서 Nested Object로 ModelAttribute에 바인딩 되기 위해서 반드시 모든 필드가 var이어야 한다.
 * class Criteria (
 *   var userName: LikeFilter = null // 반드시 var이어야 함.
 * )
 *
 * CustomBeanPropertySqlParameterSource로 바인딩 된 경우
 * LikeFilter 타입은 escape처리 및 lowercase 처리를 거쳐 변환된다.
 * @see CustomBeanPropertySqlParameterSource
 *
 * @author Lee Tae Su
 */
class LikeParameters(
    var value: String = "",
    var empty: Boolean = false,
): Filterable {
    @get:JsonIgnore
    override val filterable get() = this.value.isEmpty() || this.empty

    @get:JsonIgnore
    val chunks get() = this.value.split("%")?.filter { it.isNotBlank() } ?: emptyList()
}
