package pl.weronkagolonka.volatune

import pl.weronkagolonka.volatune.domain.Geohash
import pl.weronkagolonka.volatune.domain.Song
import pl.weronkagolonka.volatune.domain.User
import pl.weronkagolonka.volatune.domain.events.Playback
import pl.weronkagolonka.volatune.domain.fromLatLng
import java.time.Instant

fun Playback.Companion.getTestInstance(): Playback =
    Playback(
        user =
            User(
                name = "test",
                profileImageUrl = "test.jpg",
            ),
        song =
            Song(
                title = "test",
                artists = listOf("test"),
                album = "test",
                albumImageUrl = "test.jpg",
            ),
        timestamp = Instant.now().epochSecond,
        location = Geohash.fromLatLng(1.0, 2.0),
    )
