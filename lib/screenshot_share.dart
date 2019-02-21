import 'dart:async';

import 'package:flutter/services.dart';

class ScreenshotShare {
  static const MethodChannel _channel =
      const MethodChannel('screenshot_share');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<Null> takeScreenshotAndShare(String fileName) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("fileName", () => fileName);

    await _channel.invokeMethod('takeScreenshotAndShare', args);
    return null;
  }
}
