package pl.weronka.golonka.volatune.common.domain

import com.uber.h3core.H3Core
import kotlinx.serialization.Serializable
import java.lang.Math.toRadians
import kotlin.math.*

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    fun h3Index(h3: H3Core) = h3.latLngToCell(latitude, longitude, 8)
}

fun Pair<Location, Location>.areWithinProximity(proximityInMeters: Double = 500.0): Boolean {
    val (location, otherLocation) = this

//    val R = 6_371_000.0 // Earth radius in meters
//
//    // distance between latitudes and longitudes
//    val dLat = toRadians(otherLocation.latitude - location.longitude)
//    val dLon = toRadians(otherLocation.longitude - location.longitude)
//
//    // convert to radians
//    val lat1 = toRadians(location.latitude)
//    val lat2 = toRadians(otherLocation.latitude)
//
//    // apply formulae
//    val a =
//        sin(dLat / 2).pow(2.0) +
//            sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
//    val c = 2 * asin(sqrt(a))
//    return R * c

    val r = 6_371_000.0 // Earth radius in meters
    val latRad1 = toRadians(location.latitude)
    val latRad2 = toRadians(otherLocation.latitude)
    val dLat = toRadians(otherLocation.latitude - location.latitude)
    val dLon = toRadians(otherLocation.longitude - location.longitude)

    val a =
        sin(dLat / 2).pow(2.0) +
            cos(latRad1) * cos(latRad2) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distanceInMeters = r * c

    return distanceInMeters <= proximityInMeters
}
