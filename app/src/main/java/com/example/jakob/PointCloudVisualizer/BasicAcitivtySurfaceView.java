package com.example.jakob.PointCloudVisualizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.Toast;

public class BasicAcitivtySurfaceView extends GLSurfaceView {

    private final BasicActivityRender mRenderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private boolean twoPointersDown = false;

    public boolean isRenderSet() {
        return renderSet;
    }

    public void setRenderSet(boolean renderSet) {
        this.renderSet = renderSet;
    }

    private boolean renderSet;

    public BasicAcitivtySurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new BasicActivityRender(context);
        if (supportsEs2(context)) {
            setEGLContextClientVersion(2);
            renderSet = true;
        } else {
            Toast.makeText(context, "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

    private boolean supportsEs2(Context context){
        final ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                (ConfigurationInfo) activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = e.getX();
                mPreviousY = e.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                twoPointersDown = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                twoPointersDown = false ;
                break;
            case MotionEvent.ACTION_MOVE:
                if (twoPointersDown){

                } else {
                   handleRotation(e);
                }
                break;
        }
        requestRender();
        return true;
    }
    private void handleRotation(MotionEvent e){
        float x = e.getX();
        float y = e.getY();

        float dx = x - mPreviousX;
        float dy = y - mPreviousY;

        // reverse direction of rotation above the mid-line
        if (y > getHeight() / 2) {
            dx = dx * -1;
        }
        // reverse direction of rotation to left of the mid-line
        if (x < getWidth() / 2) {
            dy = dy * -1;
        }

        mRenderer.setAngle(
                mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));

        mPreviousX = x;
        mPreviousY = y;

    }
}

