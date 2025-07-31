package pl.weronka.golonka.volatune.common.domain

import com.uber.h3core.H3Core
import kotlinx.serialization.Serializable
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    fun h3Index(h3: H3Core) = h3.latLngToCell(latitude, longitude, Constants.H3_INDEX_RESOLUTION)
}

fun Pair<Location, Location>.isWithinProximity(proximityInMeters: Double = 500.0): Boolean {
    val (location, otherLocation) = this

    val latRad1 = toRadians(location.latitude)
    val latRad2 = toRadians(otherLocation.latitude)
    val dLat = toRadians(otherLocation.latitude - location.latitude)
    val dLon = toRadians(otherLocation.longitude - location.longitude)

    val a =
        sin(dLat / 2).pow(2.0) +
            cos(latRad1) * cos(latRad2) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distanceInMeters = Constants.EARTH_RADIUS_METERS * c

    return distanceInMeters <= proximityInMeters
}
