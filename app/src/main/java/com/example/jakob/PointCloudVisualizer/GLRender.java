package com.example.jakob.PointCloudVisualizer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
import com.example.jakob.PointCloudVisualizer.GlObjects.CameraGL;
import com.example.jakob.PointCloudVisualizer.GlObjects.PolyIndexBufferModelGL;
import com.example.jakob.PointCloudVisualizer.GlObjects.RemotePointClusterGL;
import com.example.jakob.PointCloudVisualizer.GlObjects.Scene;
import com.example.jakob.PointCloudVisualizer.util.FPSCounter;
import com.example.jakob.PointCloudVisualizer.util.ShaderHelper;
import com.example.jakob.PointCloudVisualizer.util.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_DEPTH_BITS;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;


public class GLRender implements GLSurfaceView.Renderer {
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


    public GLRender(Context context, DataAccessLayer dal) {
        this.context = context;
        this.dal = dal;
        rotation = new float[]{0, 0, 0};
        translation = new float[]{0, 0, 0};
        scale = 1;
        fpsCounter = new FPSCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        //flickering on LG G3
        //gl.glEnable(GL_DEPTH_TEST);
        mProgram = createOpenGlProgram();
        glUseProgram(mProgram);
        receiveLocations();
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
        glFrontFace(GL_CCW);
        scene.drawScene(aPositionLocation, aColorLocation, aSizeLocation, uMVPMatrixLocation);
        fpsCounter.logFrame();
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
