package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.user.domain.UserType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/18.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@RestController
class UserRetrieveController(private val userRetrieveService: UserRetrieveService) {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/studies/{studyKey}/users/{userKey}")
    fun retrieve(
        @PathVariable("studyKey") studyKey: Long,
        @PathVariable("userKey") userKey: Long,
    ): UserRetrieveResponse {
        // return userRetrieveService.retrieve(studyKey) // 잘못된 파라미터를 넘기고 있음
        return userRetrieveService.retrieve(userKey) // 잘못된 파라미터를 넘기고 있음
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/studies/{studyKey}/users")
    fun retrieve(
        @PathVariable("studyKey") studyKey: Long,
        @RequestParam("email") email: String,
    ): UserRetrieveResponse {
        // return userRetrieveService.retrieve(studyKey) // 잘못된 파라미터를 넘기고 있음
        return userRetrieveService.retrieve(studyKey, email) // 잘못된 파라미터를 넘기고 있음
    }
}

@Service
class UserRetrieveService {
    fun retrieve(userKey: Long): UserRetrieveResponse {
        return UserRetrieveResponse(
            key = userKey,
            email = "taesu@crscube.co.kr",
            name = "Lee",
            birthDate = LocalDate.of(1993, 2, 16),
            type = UserType.DIAMOND
        )
    }

    fun retrieve(
        studyKey: Long, email: String
    ): UserRetrieveResponse {
        return UserRetrieveResponse(
            key = 123,
            email = "taesu@crscube.co.kr",
            name = "Lee",
            birthDate = LocalDate.of(1993, 2, 16),
            type = UserType.DIAMOND
        )
    }
}

class UserRetrieveResponse(
    val key: Long,
    val email: String?,
    val name: String,
    val birthDate: LocalDate,
    val type: UserType
)