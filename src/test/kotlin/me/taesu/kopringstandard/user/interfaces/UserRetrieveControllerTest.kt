package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.app.domain.CodeEnum
import me.taesu.kopringstandard.user.domain.UserType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.*
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.snippet.AbstractDescriptor
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import kotlin.reflect.KClass


/**
 * Created by itaesu on 2022/05/18.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
// @EnabledIf(expression = "#{environment['test'] == 'webmvc-test'}", loadContext = true)
// @EnabledIfEnvironmentVariable(named = "test", matches = "webmvcTest")
// @EnabledIfEnvironmentVariable(named = "testMode", matches = "webmvcTest")
// @EnabledIfEnvironmentVariable(named = "testMode", matches = "webmvc-test")
@EnabledIfSystemProperty(
    named = "testMode",
    matches = "webmvcTest"
) // mvn -DargLine="-DtestMode=webmvcTest" clean install
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@WebMvcTest(
    value = [UserRetrieveController::class],
)
internal class UserRetrieveControllerTest {
    @MockBean
    private lateinit var userRetrieveService: UserRetrieveService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation))
            .build()
    }

    @Test
    fun `Parameter Stub`() {
        // given
        given(userRetrieveService.retrieve(12L)).willReturn(
            UserRetrieveResponse(
                key = 12L,
                email = "taesu@crscube.co.kr",
                name = "Lee",
                birthDate = LocalDate.of(1993, 2, 16),
                type = UserType.DIAMOND
            )
        )

        // when
        val perform = this.mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("/api/v1/studies/{studyKey}/users/{userKey}", 1, 12)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        perform
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.key").value(12))
            .andExpect(jsonPath("$.email").value("taesu@crscube.co.kr"))
            .andExpect(jsonPath("$.name").value("Lee"))
            .andExpect(jsonPath("$.birthDate").value("1993-02-16"))
            .andExpect(jsonPath("$.type.value").value("DIAMOND"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                prettyDocument(
                    "Retrieve User",
                    pathParameters(
                        parameterWithName("studyKey").description("스터디 키").type("Long"),
                        parameterWithName("userKey").description("사용자 키").type("Long"),
                    ),
                    responseFields(
                        "key" type NUMBER mean "사용자 키",
                        "email" type STRING mean "사용자 이메일",
                        "name" type STRING mean "사용자 이름" nullable true,
                        "birthDate" date "" mean "생년월일",
                        "type.value" enum UserType::class mean "사용자 타입의 코드 값",
                        "type.label" type STRING mean "사용자 타입의 다국어 라벨",
                    ),
                )
            )
    }

    @Test
    fun `사용자 조회 실패`() {
        // given
        given(userRetrieveService.retrieve(12L)).willThrow(
            IllegalArgumentException("존재하지 않습니다.")
        )

        // when
        val perform = this.mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("/api/v1/studies/{studyKey}/users/{userKey}", 1, 12)
                .accept(MediaType.APPLICATION_JSON)
        )

        // then
        perform.andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                prettyDocument(
                    "Retrieve User Failure",
                    pathParameters(
                        parameterWithName("studyKey").description("스터디 키").type("Long"),
                        parameterWithName("userKey").description("사용자 키").type("Long"),
                    ),
                    responseFields(
                        "errorCode" error ""
                            code "RESOURCE_NOT_FOUND" mean "존재하지 않는 사용자일 때."
                            code "INVALID_STATUS" mean "처리중 예상치 못한 상황일 때."
                            code "INVALID_REQUEST" mean "요청에 문제가 생겼을 때.",
                        "message" type STRING mean "에러메시지",
                    )
                )
            )
    }

    @Test
    fun `Parameter Stub with any, eq`() {
        // given
        given(userRetrieveService.retrieve(eq((12L)), anyString())).willReturn(
            UserRetrieveResponse(
                key = 12L,
                email = "taesu@crscube.co.kr",
                name = "Lee",
                birthDate = LocalDate.of(1993, 2, 16),
                type = UserType.DIAMOND
            )
        )

        // when
        val perform = this.mockMvc.get("/api/v1/studies/12/users") {
            param("email", "taesu@crscube.co.kr")
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.key") { value(12) }
            jsonPath("$.email") { value("taesu@crscube.co.kr") }
            jsonPath("$.name") { value("Lee") }
            jsonPath("$.birthDate") { value("1993-02-16") }
        }.andDo {
            print()
        }
    }

    @Test
    fun `Parameter Stub with any, no eq`() {
        // given
        // any와 함께 실제 값 12를 전달하면 InvalidUseOfMatchersException가 발생
        // any와 함께 쓰려면 eq(12)로 해야 함.
        try {
            given(userRetrieveService.retrieve(12, anyString())).willReturn(
                UserRetrieveResponse(
                    key = 12L,
                    email = "taesu@crscube.co.kr",
                    name = "Lee",
                    birthDate = LocalDate.of(1993, 2, 16),
                    type = UserType.DIAMOND
                )
            )
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(InvalidUseOfMatchersException::class.java)
        }
    }

    @Disabled
    @Test
    fun `Parameter Stub with only eq`() {
        // given
        // eq로만 스텁 채워도 NullPointerException, InvalidUseOfMatchersException가 발생
        try {
            given(
                userRetrieveService.retrieve(
                    ArgumentMatchers.eq(12L),
                    ArgumentMatchers.eq("taesu@crscube.co.kr")
                )
            ).willReturn(
                UserRetrieveResponse(
                    key = 12L,
                    email = "taesu@crscube.co.kr",
                    name = "Lee",
                    birthDate = LocalDate.of(1993, 2, 16),
                    type = UserType.DIAMOND
                )
            )
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(NullPointerException::class.java)
        }
    }
}

fun <T: AbstractDescriptor<T>> AbstractDescriptor<T>.type(type: String): T {
    return this.attributes(key("type").value(type))
}

fun FieldDescriptor.optionalField(): FieldDescriptor {
    this.optional()
    this.attributes(key("nullable").value(true))
    return this
}

fun prettyDocument(
    id: String,
    vararg snippets: Snippet
): RestDocumentationResultHandler {
    return document(
        id,
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint(), removeHeaders("Content-Length")),
        *snippets
    )
}

open class Field(path: String): FieldDescriptor(path)

infix fun String.type(fieldType: FieldType) = Field(this).apply {
    type(fieldType.type)
    nullable(false)
}

infix fun <T: Enum<T>> String.enum(clazz: KClass<T>) = Field(this).apply {
    type(STRING.type)
    val enums = if (CodeEnum::class.java.isAssignableFrom(clazz.java)) {
        clazz.java.enumConstants.map { it as CodeEnum }.map { "${it.name} : ${it.description}" }
    } else {
        clazz.java.enumConstants.map { it.name }
    }
    format(enums.joinToString("\r\n"))
}

infix fun String.date(stub: String) = Field(this).apply {
    type(DATE.type)
    format("YYYY-MM-DD 형식")
}

infix fun Field.mean(description: String): Field = this.apply {
    val origin = this.description ?: ""
    description("${origin}\r\n$description")
}

infix fun Field.format(format: String): Field = this.apply {
    this.attributes(key("format").value(format))
}

infix fun Field.nullable(nullable: Boolean): Field {
    if (nullable) {
        this.optional()
    }
    this.attributes(key("nullable").value(nullable.toString()))
    return this
}

class ErrorCodeField(path: String): Field(path)
class ErrorCodeFieldStatus(val field: ErrorCodeField, val errorCode: String)

infix fun String.error(stub: String) = ErrorCodeField(this).apply {
    type(ERROR_CODE.type)
    nullable(false)
}

infix fun ErrorCodeField.code(errorCode: String) = ErrorCodeFieldStatus(this, errorCode)
infix fun ErrorCodeFieldStatus.mean(description: String) = this.field.apply {
    mean("${errorCode}: $description")
}

sealed class FieldType(val type: JsonFieldType)

object ARRAY: FieldType(JsonFieldType.ARRAY)
object BOOLEAN: FieldType(JsonFieldType.BOOLEAN)
object OBJECT: FieldType(JsonFieldType.OBJECT)
object NUMBER: FieldType(JsonFieldType.NUMBER)
object STRING: FieldType(JsonFieldType.STRING)
object ERROR_CODE: FieldType(JsonFieldType.STRING)
object DATE: FieldType(JsonFieldType.STRING)