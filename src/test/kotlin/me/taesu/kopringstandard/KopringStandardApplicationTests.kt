package me.taesu.kopringstandard

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.boot.test.context.SpringBootTest

@EnabledIfSystemProperty(named = "testMode", matches = "unitTest")
@SpringBootTest
class KopringStandardApplicationTests {

    @Test
    fun contextLoads() {
    }

}
