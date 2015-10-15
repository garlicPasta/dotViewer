package com.example.jakob.PointCloudVisualizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

public class BasicAcitivtySurfaceView extends GLSurfaceView  {

    private static final String DEBUG_TAG = "Gesture";
    private final BasicActivityRender mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private float mScaleFactor;
    private boolean renderSet;
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector mScaleDetector;


    public boolean isRenderSet() {
        return renderSet;
    }

    public void setRenderSet(boolean renderSet) {
        this.renderSet = renderSet;
    }


    public BasicAcitivtySurfaceView(Context context) {
        super(context);
        mScaleFactor = 10;
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
        GestureListener gestureHandler = new GestureListener();
        mDetector = new GestureDetectorCompat(context,gestureHandler);
        mDetector.setOnDoubleTapListener(gestureHandler);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        this.mDetector.onTouchEvent(e);
        this.mScaleDetector.onTouchEvent(e);
        // Be sure to call the superclass implementation
        return true;
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


    private class GestureListener implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            if (e2.getPointerCount() == 2){
                Log.d(DEBUG_TAG, "2Finger scroll: " + e1.toString() + e2.toString());
                Log.d(DEBUG_TAG, "2Finger scroll: " + distanceX + distanceY);
                float currentTransX = mRenderer.getTransX();
                float currentTransY = mRenderer.getTransY();
                mRenderer.setTransX(currentTransX + distanceX/50);
                mRenderer.setTransY(currentTransY + distanceY/50);
            }else {
                Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
                mPreviousX += distanceX;
                mPreviousY += distanceY;
                mRenderer.setRotZ(mPreviousX);
                mRenderer.setRotY(mPreviousY);
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
            return true;
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 20.0f));
            mRenderer.setScale(mScaleFactor);
            Log.v("ScaleListeneer", String.valueOf(mScaleFactor));
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }
}

