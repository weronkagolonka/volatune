package pl.weronkagolonka.volatune.domain

import ch.hsr.geohash.GeoHash

typealias Geohash = String

fun String.Companion.geohashFromLatLng(
    latitude: Double,
    longitude: Double,
    precision: Int = 12, // get full geohash
): Geohash = GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, precision)
