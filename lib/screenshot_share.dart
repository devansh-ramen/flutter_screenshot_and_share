import 'dart:async';

import 'package:flutter/services.dart';

class ScreenshotShare {
  static const MethodChannel _channel = const MethodChannel('screenshot_share');

  static Future<Null> takeScreenshotAndShare() async {
    Map<String, dynamic> args = <String, dynamic>{};
    await _channel.invokeMethod('takeScreenshotAndShare', args);
    return null;
  }
}
