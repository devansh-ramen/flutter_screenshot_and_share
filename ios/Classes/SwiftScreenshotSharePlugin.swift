import Flutter
import UIKit

public class SwiftScreenshotSharePlugin: NSObject, FlutterPlugin {
    var controller: FlutterViewController!
    var messenger: FlutterBinaryMessenger;

    init(cont: FlutterViewController, messenger: FlutterBinaryMessenger) {
        self.controller = cont;
        self.messenger = messenger;
        super.init();
    }

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "screenshot_share", binaryMessenger: registrar.messenger())
        let app =  UIApplication.shared
        let controller : FlutterViewController = app.delegate!.window!!.rootViewController as! FlutterViewController;
        let instance = SwiftScreenshotSharePlugin.init(cont: controller, messenger: registrar.messenger())
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if (call.method.elementsEqual("takeScreenshotAndShare")) {
            takeScreenshotAndShare(view: controller.view);
        }
    }

    func takeScreenshotAndShare(view: UIView) {
        UIGraphicsBeginImageContext(view.frame.size)
        view.layer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        var imagesToShare = [AnyObject]()
        imagesToShare.append(image!)

        let activityViewController = UIActivityViewController(activityItems: imagesToShare , applicationActivities: nil)
        activityViewController.popoverPresentationController?.sourceView = view
        controller.present(activityViewController, animated: true, completion: nil)
    }
}




