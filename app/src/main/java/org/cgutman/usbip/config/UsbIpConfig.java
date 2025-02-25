package org.cgutman.usbip.config;

import org.cgutman.usbip.service.UsbIpService;
import org.cgutman.usbipserverforandroid.R;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class UsbIpConfig extends ComponentActivity {
    private final Point screenSize = new Point();
    private boolean screenSizeSet = false;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // We don't actually care if the permission is granted or not. We will launch the service anyway.
                startService(new Intent(UsbIpConfig.this, UsbIpService.class));
            });

    private void handlePenTouchOrHover(MotionEvent event) {
        int x = Math.round(event.getRawX() * event.getXPrecision());
        int y = Math.round(event.getRawY() * event.getYPrecision());
        int pressure = Math.round(event.getPressure() * 4095);
        // TODO
//        float orientation = event.getOrientation();
//        float tilt = event.getAxisValue(MotionEvent.AXIS_TILT);
        boolean buttonPrimary = event.isButtonPressed(MotionEvent.BUTTON_STYLUS_PRIMARY);
        boolean buttonSecondary = event.isButtonPressed(MotionEvent.BUTTON_STYLUS_SECONDARY);
//        System.out.println(String.format("Orientation: %f", orientation));
//        System.out.println(String.format("Tilt: %f", tilt));

        Intent broadcast = new Intent("position");
        broadcast.putExtra("x", x);
        broadcast.putExtra("y", y);
        broadcast.putExtra("pressure", pressure);
        broadcast.putExtra("buttonPrimary", buttonPrimary);
        broadcast.putExtra("buttonSecondary", buttonSecondary);

        sendBroadcast(broadcast);

        if (!screenSizeSet) {
            Intent broadcastSize = new Intent("maxSize");
            broadcastSize.putExtra("maxX", (int)Math.ceil(screenSize.x * event.getXPrecision()));
            broadcastSize.putExtra("maxY", (int)Math.ceil(screenSize.y * event.getYPrecision()));
            sendBroadcast(broadcastSize);
            screenSizeSet = true;
        }
    }

    private void handlePenWentOutOfRange() {
        Intent broadcast = new Intent("penOutOfRange");
        sendBroadcast(broadcast);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
            if (event.getAction() == MotionEvent.ACTION_HOVER_MOVE)
                handlePenTouchOrHover(event);
            if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT)
                handlePenWentOutOfRange();
            return true;
        } else {
            return super.onGenericMotionEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE && event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
            handlePenTouchOrHover(event);
            return true;
        } else {
            return super.onGenericMotionEvent(event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbip_config);

        if (ContextCompat.checkSelfPermission(UsbIpConfig.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(UsbIpConfig.this, UsbIpService.class));
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        this.getWindowManager().getDefaultDisplay().getRealSize(screenSize);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(UsbIpConfig.this, UsbIpService.class));
        super.onDestroy();
    }
}
