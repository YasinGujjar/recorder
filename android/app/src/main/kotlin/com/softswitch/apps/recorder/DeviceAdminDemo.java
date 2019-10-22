package com.softswitch.apps.recorder;

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.FROYO)
public class DeviceAdminDemo extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent service = new Intent(context, TService.class);
                context.startForegroundService(service);
            } else {
                Intent service = new Intent(context, TService.class);
                context.startService(service);
            }
        }
    }

    public void onEnabled(Context context, Intent intent) {
    };

    public void onDisabled(Context context, Intent intent) {
    };
}
