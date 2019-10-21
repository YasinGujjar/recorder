package com.softswitch.apps.recorder

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {


  val REQUEST_CODE = 5912
  @TargetApi(Build.VERSION_CODES.FROYO)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)



    // val p = packageManager

    // val componentName = ComponentName(this, MainActivity::class.java) // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
    //p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
//    //startService(new Intent(this, StartService.class));
//    //startService(new Intent(this, SmsOutgoingService.class));
    try {
      // Initiate DevicePolicyManager.
      val mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
      val mAdminName = ComponentName(this, DeviceAdminDemo::class.java)

      if (!mDPM.isAdminActive(mAdminName)) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
        startActivityForResult(intent, REQUEST_CODE)
      } else {
        // mDPM.lockNow()
        //finish()
        //                 Intent intent = new Intent(MainActivity.this,
        //                 TrackDeviceService.class);
        //                 startService(intent);
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }


  }





  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (REQUEST_CODE == requestCode) {
      // startService(Intent(this@MainActivity, TService::class.java))
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(Intent(this@MainActivity, TService::class.java))
      }
      else{
        startService(Intent(this@MainActivity, TService::class.java))
      }
      //finish()
    }
    super.onActivityResult(requestCode, resultCode, data)
  }


}
