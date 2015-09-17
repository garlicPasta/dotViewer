package com.example.jakob.PointCloudVisualizer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.jakob.PointCloudVisualizer.util.FPSCounter;
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

    static final int BYTES_PER_FLOAT = 4;
    static final int COLOR_COMPONENT_COUNT = 3;
    static final int POSITION_COMPONENT_COUNT = 3;

    private int mProgram;

    private Cube mCube;
    private PointModel lowResExample;
    private float mCubeRotation;
    private int mu_MatrixHandle;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix= new float[16];
    private int aPositionLocation;
    private int mMVPMatrixLocation;
    public int uColorLocation;
    public int aColorLocation;
    private float angle;
    private float scale;

    private FPSCounter fpsCounter;



    public BasicActivityRender(Context context) {
        this.context = context;
        angle = 0;
        scale = 10;
        fpsCounter = new FPSCounter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glEnable(GL_DEPTH_TEST);

        mProgram = createOpenGlProgram();
        glUseProgram(mProgram);
        receiveLocations();

        PlyParser plyP = new PlyParser(context, R.raw.medium_res_example);
        lowResExample = new PointModel(plyP.getVertexBuffer(), plyP.getColorBuffer());

        mCubeRotation = 0;

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
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glClear(GL_DEPTH_BITS);
        glFrontFace(GL_CCW);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 4, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        float[] mVPMatrix = new float[16];
        float[] scaleM= new float[16];

        //Matrix.scaleM(scaleM,0, 3.0f, 3.0f, 3.0f);
        Matrix.setRotateM(mModelMatrix, 0, angle, 0.5f, 0.5f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);
        //Matrix.multiplyMM(mModelMatrix, 0,mModelMatrix, 0, scaleM, 0);
        Matrix.multiplyMM(mVPMatrix, 0,  mViewMatrix, 0, mModelMatrix , 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mVPMatrix, 0);
        // get handle to shape's transformation matrix
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mMVPMatrix, 0);
        // Draw shape
        lowResExample.bindVertex(aPositionLocation);
        lowResExample.bindColor(aColorLocation);
        lowResExample.draw();
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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
