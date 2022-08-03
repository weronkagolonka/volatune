import 'package:flutter/material.dart';

import 'dart:typed_data';

import 'package:spotify_sdk/models/image_uri.dart';
import 'package:spotify_sdk/models/player_state.dart';
import 'package:spotify_sdk/spotify_sdk.dart';
import 'package:logger/logger.dart';

import '../constants/spacing.dart';
import '../common/set_status.dart';

class NowPlaying extends StatefulWidget {
  const NowPlaying({Key? key}) : super(key: key);

  @override
  State<NowPlaying> createState() => _NowPlayingState();
}

class _NowPlayingState extends State<NowPlaying> {

  late ImageUri? currentTrackImageUri;

  final Logger _logger = Logger(
  //filter: CustomLogFilter(), // custom logfilter can be used to have logs in release mode
  printer: PrettyPrinter(
    methodCount: 2, // number of method calls to be displayed
    errorMethodCount: 8, // number of method calls if stacktrace is provided
    lineLength: 120, // width of the output
    colors: true, // Colorful log messages
    printEmojis: true, // Print an emoji for each log message
    printTime: true,
  ),
  );


  @override
  Widget build(BuildContext context) {
    return (
      StreamBuilder<PlayerState>(
        stream: SpotifySdk.subscribePlayerState(),
        builder: (BuildContext context, AsyncSnapshot<PlayerState> snapshot) {

          if (snapshot.data != null) {
            print('SNAPSHOT');
            print(snapshot.data);
          }

          var track = snapshot.data?.track;
          currentTrackImageUri = track?.imageUri;
          var playerState = snapshot.data;

          var songTitle = track?.name;
          var songArtist = track?.artist.name;
          var songAlbum = track?.album.name;
          
          var isPlayingPaused = playerState?.isPaused;

        
          if (playerState == null || track == null || isPlayingPaused == true) {
            return Center(
              child: Container(),
            );
          }

          return Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Container(
                  child: spotifyImageWidget(track.imageUri),
                ),
              ),
              //spotifyImageWidget(track.imageUri),
              Expanded(
                flex: 3,
                child: Padding(
                  padding: EdgeInsets.all(Spacing.regular.value),
                  child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      songArtist!,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      songTitle!,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      songAlbum!,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
                ),
              ),
            ],
          );
        }
      )
    );
  }

  Widget spotifyImageWidget(ImageUri image) {
    return FutureBuilder(
        future: SpotifySdk.getImage(
          imageUri: image,
          dimension: ImageDimension.large,
        ),
        builder: (BuildContext context, AsyncSnapshot<Uint8List?> snapshot) {
          if (snapshot.hasData) {
            return Image.memory(snapshot.data!);
          } else if (snapshot.hasError) {
            setStatus(snapshot.error.toString(), _logger);
            return SizedBox(
              width: ImageDimension.small.value.toDouble(),
              height: ImageDimension.small.value.toDouble(),
              child: const Center(child: Text('Error getting image')),
            );
          } else {
            return SizedBox(
              width: ImageDimension.small.value.toDouble(),
              height: ImageDimension.small.value.toDouble(),
              child: const Center(child: Text('Getting image...')),
            );
          }
      });
  }

}