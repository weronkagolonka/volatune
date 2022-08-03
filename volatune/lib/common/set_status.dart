import 'package:logger/logger.dart';

void setStatus(String code, Logger logger, {String? message}) {
  var text = message ?? '';
  logger.i('$code$text');
}