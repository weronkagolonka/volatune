package pl.weronkagolonka.volatune.domain

import ch.hsr.geohash.GeoHash

typealias Geohash = String

fun String.Companion.fromLatLng(
    latitude: Double,
    longitude: Double,
    precision: Int = 7, // proximity of ~150m
): Geohash = GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, precision)
