package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.query.LikeParameters
import me.taesu.kopringstandard.app.query.ListParameters
import me.taesu.kopringstandard.app.domain.RawCode
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
    private var extendedParameterMap = mutableMapOf<String, Any>()

    init {
        setExtendedParameter(parameter)
    }

    private fun setExtendedParameter(parameter: Any) {
        val readablePropertyNames = super.getReadablePropertyNames()
        setExtendedLikeParameter(parameter, readablePropertyNames)
        setExtendedInParameter(parameter, readablePropertyNames)
    }

    private fun setExtendedLikeParameter(parameter: Any, readablePropertyNames: Array<out String>) {
        this.extendedParameterMap.putAll(resolveExtendedLikeParameterMap(readablePropertyNames, parameter))
    }

    private fun setExtendedInParameter(parameter: Any, readablePropertyNames: Array<out String>) {
        this.extendedParameterMap.putAll(resolveExtendedInParameterMap(readablePropertyNames, parameter))
    }

    /**
     * LikeParameters가 들어오는 경우 값에 존재하는 '%' 문자로 분할한 청크의 사이즈만큼
     * 프로퍼티의 이름을 기반으로 하는 배열파라미터를 추가한다.
     *
     * ex) val userName: LikeParameters = LikeParameters("test%123", false)
     * -> extendedParameterMap[userName[0]] = LikeParameters("test", false)
     * -> extendedParameterMap[userName[1]] = LikeParameters("123", false)
     */
    private fun resolveExtendedLikeParameterMap(
        readablePropertyNames: Array<out String>,
        parameter: Any,
    ): Map<String, LikeParameters> = resolveParameterPairSequence<LikeParameters>(readablePropertyNames, parameter)
        .flatMap { pair ->
            val propertyName = pair.first
            val likeFilter = pair.second

            resolveParamPairs(propertyName, likeFilter)
        }
        .toMap()


    private fun resolveParamPairs(
        propertyName: String,
        likeFilter: LikeParameters,
    ) = likeFilter.chunks.mapIndexed { index, it ->
        "$propertyName[$index]" to LikeParameters(it, likeFilter.empty)
    }

    /**
     * 1000개 초과의 컬렉션 파라미터가 들어오는 경우 1000개로 나뉜
     * 프로퍼티의 이름을 기반으로 하는 배열파라미터를 추가한다.
     *
     * ex) val keys = (1L..2000L).toList()
     * -> extendedParameterMap[_keys[0]] = (1L..1000L).toList()
     * -> extendedParameterMap[_keys[1]] = (1001L..2000L).toList()
     *
     * keys[0]과 같이 원본 프로퍼티 이름을 그대로 사용하는경우 배열이 아닌 단일 값이 바인딩 됨
     * ex) keys[0] == 1L
     */
    private fun resolveExtendedInParameterMap(
        readablePropertyNames: Array<out String>,
        parameter: Any,
    ): Map<String, List<Any>> = resolveParameterPairSequence<ListParameters<*>>(readablePropertyNames, parameter)
        .map { it.first to it.second.value }
        .flatMap { pair ->
            val propertyName = pair.first
            val inFilter = pair.second?.filterNotNull()?.toSet() ?: emptySet()

            inFilter.chunked(1000).mapIndexed { index, chunk ->
                "_$propertyName[$index]" to chunk
            }
        }
        .toMap()

    /**
     * @return 제네릭 파라미터 T에 해당하는 파라미터이름, 파라미터로 구성된 Pair Sequence
     */
    private inline fun <reified T> resolveParameterPairSequence(
        readablePropertyNames: Array<out String>,
        parameter: Any,
    ): Sequence<Pair<String, T>> = readablePropertyNames
        .asSequence()
        .mapNotNull { parameterName ->
            val value = try {
                PropertyAccessorFactory
                    .forBeanPropertyAccess(parameter)
                    .getPropertyValue(parameterName)
                    .takeIf { it is T }
            } catch (e: Exception) {
                null
            }
            value?.let { parameterName to value }
        }
        .filterIsInstance<Pair<String, T>>() // 상위 시퀀스에서 걸러냈지만 컴파일러에 힌트를 주기 위해 추가

    override fun hasValue(paramName: String): Boolean {
        return extendedParameterMap.containsKey(paramName) || super.hasValue(paramName)
    }

    override fun getValue(paramName: String): Any? {
        val parameter = extendedParameterMap[paramName] ?: super.getValue(paramName)
        val value = if (parameter is ListParameters<*>) {
            parameter.value?.filterNotNull()?.filterNot { it == "" }
        } else {
            parameter
        }
        return when {
            /*
            Like 검색을 위해 String을 바인딩하여 이곳에서 escape, lower 등의 가공을 하지 말 것.
            equal 검색 등이 불가함
             */
            value is String -> value

            value is LikeParameters -> value?.value?.lowercase().escapeLikeValue()

            value is LocalDate -> value.format(DateTimeFormatter.ISO_DATE)

            value is LocalDateTime -> value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            value is RawCode -> value.code

            isInstanceOfRawCodeCollection(value) ->
                (value as Collection<*>).filterIsInstance(RawCode::class.java).map { it.code }

            else -> value
        }
    }

    /**
     * @return Like 쿼리 시 escape 된 파라미터
     */
    private fun String?.escapeLikeValue(): String? {
        return this
            ?.trim()
            ?.replace("\\", "\\\\")
            ?.replace("%", "\\%")
            ?.replace("_", "\\_")
    }


    private fun isInstanceOfRawCodeCollection(value: Any?): Boolean {
        return when (value) {
            is Collection<*> -> {
                if (value.isEmpty()) return false
                val first = value.first()
                first is RawCode
            }

            else -> false
        }
    }

    override fun getSqlType(paramName: String): Int {
        return if (extendedParameterMap.containsKey(paramName)) {
            StatementCreatorUtils.javaTypeToSqlParameterType(extendedParameterMap[paramName]!!::class.java)
        } else {
            super.getSqlType(paramName)
        }
    }
}
