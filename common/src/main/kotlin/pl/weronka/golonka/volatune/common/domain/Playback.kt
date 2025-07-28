package pl.weronka.golonka.volatune.common.domain

import com.github.avrokotlin.avro4k.Avro
import kotlinx.serialization.Serializable
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import pl.weronka.golonka.volatune.common.domain.Geohash
import pl.weronkagolonka.volatune.domain.Song
import pl.weronkagolonka.volatune.domain.User
import java.time.Instant

@Serializable
data class Playback(
    val user: User,
    val song: Song,
    val timestamp: Long,
    val location: Geohash,
) {
    companion object {
        fun getTestInstance(): Playback =
            Playback(
                user =
                    User(
                        id = "1",
                        name = "test",
                        profileImageUrl = "test.jpg",
                    ),
                song =
                    Song(
                        id = "1",
                        title = "test",
                        artists = listOf("test"),
                        album = "test",
                        albumImageUrl = "test.jpg",
                    ),
                timestamp = Instant.now().epochSecond,
                location = Geohash.fromLatLng(1.0, 2.0),
            )
    }
}

/**
 * Based on example in Avro4k [issue](https://github.com/avro-kotlin/avro4k/issues/1#issuecomment-1127551524)
 *
 * Consider using Avro4k Serde [library](https://github.com/thake/avro4k-kafka-serializer) if more schemas are to be added.
 */
class PlaybackKafkaSerializer :
    Serializer<Playback>,
    Deserializer<Playback> {
    private val serializer = Playback.serializer()

    override fun deserialize(
        topic: String?,
        data: ByteArray?,
    ): Playback? =
        data?.let {
            Avro.decodeFromByteArray(serializer, data)
        }

    override fun serialize(
        topic: String?,
        data: Playback?,
    ): ByteArray? =
        data?.let {
            Avro.encodeToByteArray(serializer, data)
        }

    override fun configure(
        configs: Map<String?, *>?,
        isKey: Boolean,
    ) {}

    override fun close() {}
}
