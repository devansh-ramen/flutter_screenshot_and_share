package devansh.perfectbuzz.screenshotshare;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

/** ScreenshotSharePlugin */
public class ScreenshotSharePlugin implements MethodCallHandler {

  private static final String TAG = "Screenshot-Plugin";

  private Registrar registrar;
  private Activity activity;
  private FlutterView flutterView;
  private MethodChannel channel;
  private final int WRITE_ACCESS_REQUEST_ID = 12;

  private String fileName;

  public ScreenshotSharePlugin(Registrar registrar, Activity activity, FlutterView flutterView, MethodChannel channel) {
    this.registrar = registrar;
    this.activity = activity;
    this.flutterView = flutterView;
    this.channel = channel;
    this.channel.setMethodCallHandler(this);
    setRequestPermissionListener();
  }

  private void setRequestPermissionListener() {

    registrar.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
      @Override
      public boolean onRequestPermissionsResult(int i, String[] strings, int[] ints) {
        if (i == WRITE_ACCESS_REQUEST_ID) {

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
              takeScreenshot();
              return true;
            } else {

              Log.v(TAG, "Permission is revoked");
              activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
              return false;
            }
          }
        }
        return false;
      }
    });
  }


  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "screenshot_share");
    channel.setMethodCallHandler(new ScreenshotSharePlugin(registrar, registrar.activity(), registrar.view(), channel));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("takeScreenshotAndShare")) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activity.requestPermissions(new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_ACCESS_REQUEST_ID);
      }

    } else {
      result.notImplemented();
    }
  }

  private void takeScreenshot() {
    try {
      // image naming and path  to include sd card  appending name you choose for file
      java.util.Date date =  new java.util.Date();
      android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
      String fileName = "screenshot-" + date;

      String mPath = Environment.getExternalStorageDirectory().toString() + "/" + fileName + ".jpg";

      // create bitmap screen capture
      View v1 = activity.getWindow().getDecorView().getRootView();

      v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
              View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());

      v1.setDrawingCacheEnabled(true);
      //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

      Bitmap bitmap = flutterView.getBitmap();

      v1.setDrawingCacheEnabled(false);

      File imageFile = new File(mPath);

      FileOutputStream outputStream = new FileOutputStream(imageFile);
      int quality = 100;
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
      outputStream.flush();
      outputStream.close();

      openScreenshot(imageFile);
    } catch (Throwable e) {
      // Several error may come out with file handling or DOM
      e.printStackTrace();
    }
  }

  private void openScreenshot(File imageFile) {
    StrictMode.VmPolicy.Builder builder = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
      builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
    }

    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(imageFile);
    intent.setDataAndType(uri, "image/*");
    activity.startActivity(intent);
  }
}
