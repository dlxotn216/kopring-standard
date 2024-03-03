package me.taesu.kopringstandard

import me.taesu.kopringstandard.user.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
class KopringStandardApplication {
    @PostConstruct
    fun onConstruct() {
        setTimezone()
    }

    companion object {
        const val DEFAULT_ZONE_ID = "UTC"
    }
}

fun main(args: Array<String>) {
    setTimezone()
    runApplication<KopringStandardApplication>(*args)
}

fun setTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone(KopringStandardApplication.DEFAULT_ZONE_ID))
}

@Profile("!test")
@Component
class TestMockRunner(private val userRepository: UserRepository): ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        userRepository.saveAll(listOf(
            User(
                userInfo = UserInfo(
                    "taesu@crscube.co.kr",
                    "lee taesu",
                    LocalDate.of(1993, 2, 16),
                    UserType.DIAMOND,
                    null,
                    null,
                    UserStatus.ACTIVE
                )
            ),
            User(
                userInfo = UserInfo(
                    "kim@crscube.co.kr",
                    "kim",
                    LocalDate.of(1996, 2, 16),
                    UserType.DIAMOND,
                    null,
                    null,
                    UserStatus.ACTIVE
                )
            ),
            User(
                userInfo = UserInfo(
                    "park@com.com",
                    "park kim",
                    LocalDate.of(1996, 2, 16),
                    UserType.BRONZE,
                    null,
                    null,
                    UserStatus.ACTIVE
                )
            )
        ))
    }
}
