package pl.weronka.golonka.volatune.common.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class LocationTest :
    DescribeSpec({
        val location1 = Location(60.2656884, 11.176016)
        val location2 = Location(50.2716136, 18.9684973)

        it("should return true if locations are within proximity") {
            val proximity = 1500.0 * 1000 // 1500km

            (location1 to location2).isWithinProximity(proximity) shouldBe true
        }

        it("should false true if locations are further away than given proximity") {
            val proximity = 500.0

            (location1 to location2).isWithinProximity(proximity) shouldBe false
        }
    })
