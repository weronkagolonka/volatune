package pl.weronka.golonka.volatune.common.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class GeohashTest :
    DescribeSpec({
        it("should produce geohash from latitude and longitude") {
            val expectedGeohash = "u4xv669"
            val latitude = 60.0
            val longitude = 11.0

            val actualGeohash =
                Geohash.fromLatLng(
                    latitude = latitude,
                    longitude = longitude,
                    7,
                )
            actualGeohash shouldBe expectedGeohash
        }
    })
