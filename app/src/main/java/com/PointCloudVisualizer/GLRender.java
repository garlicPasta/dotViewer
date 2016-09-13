package com.PointCloudVisualizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;

import com.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
import com.PointCloudVisualizer.GlObjects.CameraGL;
import com.PointCloudVisualizer.GlObjects.RemotePointClusterGL;
import com.PointCloudVisualizer.GlObjects.Scene;
import com.PointCloudVisualizer.util.FPSCounter;
import com.PointCloudVisualizer.util.PiCounter;
import com.PointCloudVisualizer.util.ShaderHelper;
import com.PointCloudVisualizer.util.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_DEPTH_BITS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;


public class GLRender implements GLSurfaceView.Renderer {
    private static final float IDLE_ROTATION_SPEED = 2f;
    private static final float IDLE_ROTATION_AMPLITUDE = 0.04f;
    private final Context context;

    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String A_SIZE = "a_Size";
    private static final String U_MATRIX = "u_Matrix";

    public static final int COLOR_COMPONENT_COUNT = 3;
    public static final int POSITION_COMPONENT_COUNT = 3;
    public static final int SIZE_COMPONENT_COUNT = 1;

    private int mProgram;

    private Scene scene;
    CameraGL camera;
    private FPSCounter fpsCounter;
    private DataAccessLayer dal;

    private int aPositionLocation;
    private int uMVPMatrixLocation;
    public int aColorLocation;
    public int aSizeLocation;

    private float[] rotation;
    private float[] translation;
    private float scale;

    private PiCounter piCounter;

    public GLRender(Context context, DataAccessLayer dal) {
        this.context = context;
        this.dal = dal;
        rotation = new float[]{0, 0, 0};
        translation = new float[]{0, 0, 0};
        scale = 1;
        fpsCounter = new FPSCounter();
        piCounter = new PiCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        //flickering on LG G3
        //gl.glEnable(GL_DEPTH_TEST);
        mProgram = createOpenGlProgram();
        glUseProgram(mProgram);
        receiveLocations();
        dal.setServerUrl(PreferenceManager.
                getDefaultSharedPreferences(context).
                getString("serverIP", "Invalid IP"));
        camera = new CameraGL();
        scene = new Scene(gl);
        scene.setCamera(camera);
        scene.addModel(new RemotePointClusterGL(dal));
    }

    private void receiveLocations(){
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        aSizeLocation = glGetAttribLocation(mProgram, A_SIZE);
        uMVPMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        camera.updateRation(ratio);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BITS);
        float[] backgroundColor = readBackgroundFromSettings();
        glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
        glFrontFace(GL_CCW);
        idleRotation();
        scene.drawScene(aPositionLocation, aColorLocation, aSizeLocation, uMVPMatrixLocation);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    private void idleRotation() {
        scene.rotateSceneNoUpdate(getIdleRotationAngles(
                IDLE_ROTATION_AMPLITUDE,
                IDLE_ROTATION_SPEED
        ));
    }

    private float[] getIdleRotationAngles(float amplitude, float speed) {
        piCounter.setSpeed(speed);
        float x = piCounter.getTimeValue();
        return new float[]{
                (float) Math.sin(x) * amplitude,
                (float) Math.sin(x) * amplitude,
                0f
        };
    }

    private float[] readBackgroundFromSettings() {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        int color = p.getInt("backgroundColor", 0);
        float[] rgb = new float[4];
        rgb[0] = Color.red(color) / 255f;
        rgb[1] = Color.green(color) / 255f;
        rgb[2] = Color.blue(color) / 255f;
        rgb[3] = Color.alpha(color) / 255f;
        return rgb;
    }

    public int createOpenGlProgram(){
        String vertexShader = TextResourceReader.
                readTextFileFromResource(context, R.raw.simple_vertex_shader);
        int vertexShaderId = ShaderHelper.compileVertexShader(vertexShader);
        String fragmentShader = TextResourceReader.
                readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShader);
        return ShaderHelper.linkProgram(fragmentShaderId, vertexShaderId);
    }

    public float[] getTranslation() {
        return translation;
    }

    public void setTranslation(float[] translation) {
        this.translation = translation;
    }

    public void addToTranslation(float[] translation) {
        scene.translateScene(translation);
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        scene.rotateScene(rotation);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        scene.scaleScene(scale);
    }
}
