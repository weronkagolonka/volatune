# volatune

This is a new Kotlin project featuring an app for sharing the music stream with others.

## Goals

- The app is supposed to consolidate all major streaming services, such as Spotify, Tidal, Apple Music, etc.
- The user should be able to give consent to share her music, and/or name. If the stream is being shared, the user should be able to see the list of songs played by others (who also agreed to share their stream) in a given distance

Components
- Backend application that connects to streaming API
and polls the playback
  - The polled playback is published to kafka topic by a producer
- Application that listens to the kafka topic
and returns the list of playbacks in the given area
- web client that connects to backend applications via web socket
- in the future: mobile clients