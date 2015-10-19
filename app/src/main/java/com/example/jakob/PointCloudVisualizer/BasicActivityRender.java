package com.example.jakob.PointCloudVisualizer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.jakob.PointCloudVisualizer.util.FPSCounter;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;
import com.example.jakob.PointCloudVisualizer.util.PlyParser;
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


public class BasicActivityRender implements GLSurfaceView.Renderer {
    private final Context context;

    private static final String A_POSITION = "a_Position";
    public static final String U_COLOR = "u_Color";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";

    static final int COLOR_COMPONENT_COUNT = 3;
    static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgram;

    private Cube mCube;
    private PointModelGL model;
    private PointModelGL centerPoint;
    private Cube plane;
    private FPSCounter fpsCounter;

    private float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix= new float[16];
    private final float[] mRotMatrixX= new float[16];
    private final float[] mRotMatrixY= new float[16];
    private float[] mRotMatrix= new float[16];
    private int aPositionLocation;
    private int mMVPMatrixLocation;
    public int uColorLocation;
    public int aColorLocation;

    private float rotX;
    private float rotY;
    private float rotZ;
    private float scale;
    private float transX;
    private float transY;
    private float transZ;
    private float[] centroid;


    public BasicActivityRender(Context context) {
        this.context = context;
        rotX = 0;
        rotY = 0;
        scale = 1;
        transX = 0;
        transY = 0;
        transZ = 0;
        fpsCounter = new FPSCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glEnable(GL_DEPTH_TEST);

        mProgram = createOpenGlProgram();
        glUseProgram(mProgram);
        receiveLocations();
        PlyParser plyP = new PlyParser(context, R.raw.low_res_example);
        model = new PointModelGL(plyP.getVertexBuffer(), plyP.getColorBuffer());
        centroid = model.getCentroid();
    }

    public void receiveLocations(){
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        uColorLocation = glGetUniformLocation(mProgram, U_COLOR);
        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        mMVPMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX );
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 20000);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BITS);
        glFrontFace(GL_CCW);
        float[] mMVMatrix = new float[16];
        float[] transMatrix = new float[16];
        float[] scaleMatrix= new float[16];
        float[] centerMatrix = new float[16];
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -10.0f, 0f, 0f, 1f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);
        Matrix.setIdentityM(transMatrix, 0);
        Matrix.translateM(transMatrix, 0, transX, transY, transZ);
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.translateM(centerMatrix, 0, -centroid[0], -centroid[1], -centroid[2]);
        mRotMatrix = rotationMatrix(rotX, rotY, rotZ);
        mMVPMatrix = MatrixHelper.multMatrices(mProjectionMatrix,
                mViewMatrix,
                transMatrix,
                mRotMatrix,
                scaleMatrix,
                centerMatrix);
        GLES20.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mMVPMatrix, 0);
        // Draw shape
        model.bindVertex(aPositionLocation);
        model.bindColor(aColorLocation);
        model.draw();
        fpsCounter.logFrame();
    }

    private float[] rotationMatrix(float rotX, float rotY, float rotZ){
        float[] mRotMatrixX = new float[16];
        float[] mRotMatrixY = new float[16];
        float[] mRotMatrix =  new float[16];

        Matrix.setRotateM(mRotMatrixX, 0, rotY, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(mRotMatrixY, 0, rotZ, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mRotMatrix, 0, mRotMatrixX, 0, mRotMatrixY, 0);
        return mRotMatrix;
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

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getTransX() {
        return transX;
    }

    public void setTransX(float transX) {
        this.transX = transX;
    }

    public float getTransY() {
        return transY;
    }

    public void setTransY(float transY) {
        this.transY = transY;
    }
}
