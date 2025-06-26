package pl.weronkagolonka.volatune.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class GeohashTest :
    DescribeSpec({
        it("should produce geohash from latitude and longitude") {
            val expectedGeohash = "u4xv669vqddm"
            val latitude = 60.0
            val longitude = 11.0

            val actualGeohash =
                Geohash.geohashFromLatLng(
                    latitude = latitude,
                    longitude = longitude,
                )
            actualGeohash shouldBe expectedGeohash
        }
    })
