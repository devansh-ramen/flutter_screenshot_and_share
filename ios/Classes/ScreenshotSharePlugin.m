#import "ScreenshotSharePlugin.h"
#import <screenshot_share/screenshot_share-Swift.h>

@implementation ScreenshotSharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftScreenshotSharePlugin registerWithRegistrar:registrar];
}
@end
