package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.query.LikeParameters
import me.taesu.kopringstandard.app.query.ListParameters
import me.taesu.kopringstandard.app.domain.RawCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class CustomBeanPropertySqlParameterSourceTest {
    @Test
    fun `String 유형의 값은 getValue 호출 시 그대로 반환된다`() {
        // given
        val bean = TestStringBean("Test", "T e s t", "T%e%s_t")

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("value")).isEqualTo("Test")
        assertThat(sut.getValue("valueHasBlank")).isEqualTo("T e s t")
        assertThat(sut.getValue("valueNotEscaped")).isEqualTo("T%e%s_t")
    }

    @Test
    fun `RawCode 유형의 값은 getValue 호출 시 code가 반환된다`() {
        // given
        val bean = TestJavaBean(TestCode.OPT_1, ListParameters(listOf(TestCode.OPT_2, TestCode.OPT_3)))

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("testValue")).isEqualTo("1")
        assertThat(sut.getValue("testCollection")).isEqualTo(listOf("2", "3"))
    }

    @Test
    fun `RawCode? 유형의 값은 getValue 호출 시 null이 반환된다`() {
        // given
        val bean = TestJavaBean(null, null)

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("testValue")).isNull()
        assertThat(sut.getValue("testCollection")).isNull()
    }

    @Test
    fun `LikeParameters 유형의 파라미터는 배열 형태로 확장 된다`() {
        // given
        val bean = TestLikeParametersBean(LikeParameters("test"))

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        val value = sut.getValue("userName")
        assertThat(value).isEqualTo("test")
        assertThat(sut.getValue("userName[0]")).isEqualTo("test")
    }

    @Test
    fun `% 구문을 가진 LikeParameters 유형의 파라미터는 개수만큼 배열 형태로 확장 된다`() {
        // given
        val bean = TestLikeParametersBean(LikeParameters("test%wer")) // like test or like wer

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("userName")).isEqualTo("test\\%wer") // 원본 파라미터는 escae 됨
        assertThat(sut.getValue("userName[0]")).isEqualTo("test")
        assertThat(sut.getValue("userName[1]")).isEqualTo("wer")
    }

    @Test
    fun `공백을 포함하는 % 구문을 가진 LikeParameters 유형의 파라미터는 개수만큼 배열 형태로 확장 된다`() {
        // given
        val bean = TestLikeParametersBean(LikeParameters(" % test%wer %")) // like test or like wer

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("userName")).isEqualTo("\\% test\\%wer \\%") // 원본 파라미터는 escae 및 trim
        assertThat(sut.getValue("userName[0]")).isEqualTo("test")   // trim
        assertThat(sut.getValue("userName[1]")).isEqualTo("wer")    // trim
    }

    @Test
    fun `1000개를 초과하는 컬렉션이 존재하는 경우 분리되어 배열 형태로 확장된다`() {
        // given
        val bean = TestInFilterBean(ListParameters((1L..10000L).toList()))

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        assertThat(sut.getValue("_keys[0]")).isEqualTo((1L..1000L).toList())
    }

    @Test
    fun `LocalDateTime 형식인 경우 yyyy-MM-dd HH mm ss' 형식으로 변환된다`() {
        // given
        val bean = TestLocalDateTime(LocalDateTime.of(2021, 1, 1, 1, 1, 1))

        // when
        val sut = CustomBeanPropertySqlParameterSource(bean)

        // then
        // yyyy-MM-dd HH:mm:ss 형식으로 변환됨
        assertThat(sut.getValue("value")).isEqualTo("2021-01-01 01:01:01")
    }
}

class TestStringBean(
    val value: String,
    val valueHasBlank: String,
    val valueNotEscaped: String,
)

class TestJavaBean(
    val testValue: TestCode?,
    val testCollection: ListParameters<TestCode>?,
)

class TestLocalDateTime(
    val value: LocalDateTime,
)

enum class TestCode(override val code: String): RawCode {
    OPT_1("1"),
    OPT_2("2"),
    OPT_3("3"),
    ;
}

class TestLikeParametersBean(var userName: LikeParameters)
class TestInFilterBean(val keys: ListParameters<Long>)
