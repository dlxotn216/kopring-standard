package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.query.DateRangeParameters
import me.taesu.kopringstandard.app.query.DateRangeParametersColumnType
import me.taesu.kopringstandard.app.query.LikeParameters
import me.taesu.kopringstandard.app.query.ListParameters
import me.taesu.kopringstandard.app.vo.KeyPair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class OperatorExtensionsKtTest {
    @Test
    fun `like 필터 구문 테스트`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(value = "keyword")

        // when
        val clause = column.like("userName", likeFilter, operator = "and")

        // then
        assertThat(clause).isEqualTo("and lower(user_name) like '%'||:userName||'%' escape '\\'")
    }

    @Test
    fun `%를 포함하는 like 복합 필터 구문 테스트`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(value = "key%word")

        // when
        val clause = column.like("userName", likeFilter, operator = "and")

        // then
        assertThat(clause).isEqualTo("and (lower(user_name) like '%'||:userName[0]||'%' escape '\\' or lower(user_name) like '%'||:userName[1]||'%' escape '\\')")
    }

    @Test
    fun `%만 값으로 갖는 like 복합 필터 구문 테스트`() {
        // given
        val column = "user_name"
        val likeFilter1 = LikeParameters(value = "%")
        val likeFilter2 = LikeParameters(value = "%%")
        val likeFilter3 = LikeParameters(value = "% %")
        val likeFilter4 = LikeParameters(value = " % % ")

        // when
        val clause1 = column.like("userName", likeFilter1, operator = "and")
        val clause2 = column.like("userName", likeFilter2, operator = "and")
        val clause3 = column.like("userName", likeFilter3, operator = "and")
        val clause4 = column.like("userName", likeFilter4, operator = "and")

        // then
        assertThat(clause1).isEqualTo("")
        assertThat(clause2).isEqualTo("")
        assertThat(clause3).isEqualTo("")
        assertThat(clause4).isEqualTo("")
    }

    @Test
    fun `%를 양 끝에 값으로 갖는 like 복합 필터 구문 테스트`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(value = "%가%")

        // when
        val clause = column.like("userName", likeFilter, operator = "and")

        // then
        assertThat(clause).isEqualTo("and (lower(user_name) like '%'||:userName[0]||'%' escape '\\')")
    }

    @Test
    fun `null string이 있는 like 필터 구문은 not equal 구문이 추가 된다`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(value = "key%word")

        // when
        val clause = column.like("userName", likeFilter, operator = "and", nullString = "-")

        // then
        assertThat(clause).isEqualTo("and ((lower(user_name) like '%'||:userName[0]||'%' escape '\\' or lower(user_name) like '%'||:userName[1]||'%' escape '\\') and user_name <> '-')")
    }

    @Test
    fun `null string이 있는 like 복합 필터 구문은 not equal  구문이 추가 된다`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(value = "keyword")

        // when
        val clause = column.like("userName", likeFilter, operator = "and", nullString = "-")

        // then
        assertThat(clause).isEqualTo("and (lower(user_name) like '%'||:userName||'%' escape '\\' and user_name <> '-')")
    }

    @Test
    fun `LikeParameters의 Empty가 true라면 is null 구문을 반환한다`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(empty = true)

        // when
        val clause = column.like("userName", likeFilter, operator = "and")

        // then
        assertThat(clause).isEqualTo("and user_name is null")
    }

    @Test
    fun `LikeParameters의 Empty가 true이고 nullString이 있다면 확장된 is null 구문을 반환한다`() {
        // given
        val column = "user_name"
        val likeFilter = LikeParameters(empty = true)

        // when
        val clause = column.like("userName", likeFilter, operator = "and", nullString = "-")

        // then
        assertThat(clause).isEqualTo("and (user_name is null or user_name = '-')")
    }

    @Test
    fun `null을 포함하는 경우 or is null 구문이 추가 된다`() {
        // given
        val userKeys = ListParameters(listOf(1, 2, 3, 4, 5, null))

        // when
        val clause = "user_key".`in`("userKeys", userKeys)

        // then
        assertThat(clause).isEqualTo("and (user_key in (:_userKeys[0]) or user_key is null)")
    }

    @Test
    fun `empty string을 포함하는 경우 or is null 구문이 추가 된다`() {
        // given
        val userKeys = ListParameters(listOf(1, 2, 3, 4, 5, ""))

        // when
        val clause = "user_key".`in`("userKeys", userKeys)

        // then
        assertThat(clause).isEqualTo("and (user_key in (:_userKeys[0]) or user_key is null)")
    }

    @Test
    fun `empty string만 포함하는 경우 or is null 구문만 추가 된다`() {
        // given
        val userKeys = ListParameters(listOf(""))

        // when
        val clause = "user_key".`in`("userKeys", userKeys)

        // then
        assertThat(clause).isEqualTo("and user_key is null")
    }

    @Test
    fun `1000개 초과의 list가 주어지는 경우 or 절로 분리 된다`() {
        // given
        val userKeys = ListParameters((1L..2000L).toList())

        // when
        val clause = "user_key".`in`("userKeys", userKeys)

        // then
        assertThat(clause).isEqualTo("and (user_key in (:_userKeys[0]) or user_key in (:_userKeys[1]))")
    }

    @Test
    fun `1000개 초과의 list와 null이 주어지는 경우 or 절로 분리 된다`() {
        // given
        val userKeys = ListParameters((1L..2000L).toList() + listOf(null))

        // when
        val clause = "user_key".`in`("userKeys", userKeys)

        // then
        assertThat(clause).isEqualTo("and (user_key in (:_userKeys[0]) or user_key in (:_userKeys[1]) or user_key is null)")
    }

    @Test
    fun `KeyPair의 in 조건 테스트`() {
        // given
        val keyPairs = listOf(KeyPair(1, 2), KeyPair(3, 4))

        // when
        val clause = keyPairIn("key1", "key2", keyPairs)

        // then
        assertThat(clause).isEqualTo("and (key1, key2) in ((1, 2),(3, 4))")
    }

    @Test
    fun `KeyPair의 not in 조건 테스트`() {
        // given
        val keyPairs = listOf(KeyPair(1, 2), KeyPair(3, 4))

        // when
        val clause = keyPairNotIn("key1", "key2", keyPairs)

        // then
        assertThat(clause).isEqualTo("and (key1, key2) not in ((1, 2),(3, 4))")
    }


    @Test
    fun `Column type이 String인 DateRangeParameters between 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            startDate = LocalDate.of(2023, 9, 1),
            endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.STRING)

        // then
        assertThat(clause).isEqualTo("and poc_date between to_char(:pocDate.startDate, 'YYYY-MM-DD') and to_char(:pocDate.endDate, 'YYYY-MM-DD')")
    }

    @Test
    fun `Column type이 String인 DateRangeParameters ge startDate 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            startDate = LocalDate.of(2023, 9, 1),
            // endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.STRING)

        // then
        assertThat(clause).isEqualTo("and poc_date >= to_char(:pocDate.startDate, 'YYYY-MM-DD')")
    }

    @Test
    fun `Column type이 String인 DateRangeParameters le endDate 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            // startDate = LocalDate.of(2023, 9, 1),
            endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.STRING)

        // then
        assertThat(clause).isEqualTo("and poc_date <= to_char(:pocDate.endDate, 'YYYY-MM-DD')")
    }

    @Test
    fun `Column type이 String인 DateRangeParameters empty 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            // startDate = LocalDate.of(2023, 9, 1),
            // endDate = LocalDate.of(2023, 12, 31),
            empty = true
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.STRING)

        // then
        assertThat(clause).isEqualTo("and poc_date is null")
    }

    @Test
    fun `Column type이 DATE인 DateRangeParameters between 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            startDate = LocalDate.of(2023, 9, 1),
            endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.DATE)

        // then
        assertThat(clause).isEqualTo("and poc_date between :pocDate.startDate and to_date(to_char(:pocDate.endDate, 'YYYY-MM-DD') || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS')")
    }

    @Test
    fun `Column type이 DATE인 DateRangeParameters ge startDate 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            startDate = LocalDate.of(2023, 9, 1),
            // endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.DATE)

        // then
        assertThat(clause).isEqualTo("and poc_date >= :pocDate.startDate")
    }

    @Test
    fun `Column type이 DATE인 DateRangeParameters le endDate 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            // startDate = LocalDate.of(2023, 9, 1),
            endDate = LocalDate.of(2023, 12, 31),
            empty = false
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.DATE)

        // then
        assertThat(clause).isEqualTo("and poc_date <= to_date(to_char(:pocDate.endDate, 'YYYY-MM-DD') || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS')")
    }

    @Test
    fun `Column type이 DATE인 DateRangeParameters empty 구문 테스트`() {
        // given
        val filter = DateRangeParameters(
            // startDate = LocalDate.of(2023, 9, 1),
            // endDate = LocalDate.of(2023, 12, 31),
            empty = true
        )

        // when
        val clause = "poc_date".dateRange("pocDate", filter, DateRangeParametersColumnType.DATE)

        // then
        assertThat(clause).isEqualTo("and poc_date is null")
    }
}
