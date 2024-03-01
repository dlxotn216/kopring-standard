package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.user.domain.UserStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@RestController
class UserStatusTestController {
    @PostMapping("/api/v1/users/status")
    fun post(@RequestBody userStatusRequest: UserStatusRequest): UserStatusResponse {
        return UserStatusResponse(userStatusRequest.status)
    }
}

class UserStatusRequest(
    val status: UserStatus,
)

class UserStatusResponse(
    val status: UserStatus,
)
