package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;

import com.example.jakob.PointCloudVisualizer.util.BufferHelper;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.GLRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.GLRender.POSITION_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static com.example.jakob.PointCloudVisualizer.GLRender.SIZE_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildFloatBuffer;

public abstract class ModelGL {
    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mColorBuffer;
    private FloatBuffer mSizeBuffer;
    private float scale;
    private int vertexCount;

    float[] modelMatrix;
    float[] transMatrix;
    float[] rotationMatrix;
    float[] scaleMatrix;
    float[] centerMatrix;

    ModelGL(FloatBuffer vertices){
        scale = 1;
        mVertexBuffer = vertices;
        mVertexBuffer.position(0);
        if (mColorBuffer == null)
            mColorBuffer = BufferHelper.buildFloatBuffer(new float[vertices.capacity()]);
        if (mSizeBuffer == null) {
            float[] fb = new float[vertices.capacity()/3];
            for (int i = 0; i < fb.length; i++) {
                fb[i] = 1;
            }
            mSizeBuffer = BufferHelper.buildFloatBuffer(fb);
        }
        initMatrices();
        vertexCount = vertices.capacity() / 3;
    }

    ModelGL(FloatBuffer vertices, FloatBuffer colors) {
        this(vertices);
        mColorBuffer = colors;
        mColorBuffer.position(0);
    }

    ModelGL(FloatBuffer vertices, FloatBuffer colors, FloatBuffer size) {
        this(vertices, colors);
        mSizeBuffer = size;
        mSizeBuffer.position(0);
    }

    ModelGL(float[] vertices) {
        this(buildFloatBuffer(vertices, 3));
    }

    ModelGL(float[] vertices, float[] colors) {
        this(buildFloatBuffer(vertices, 4), buildFloatBuffer(colors, 4));
    }

    private void initMatrices(){
        scaleMatrix = new float[16];
        modelMatrix = new float[16];
        transMatrix = new float[16];
        centerMatrix = new float[16];
        rotationMatrix = new float[16];
        Matrix.setIdentityM(transMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);
    }

    public void bindVertex(int aPositionLocation){
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.mVertexBuffer);
        glEnableVertexAttribArray(aPositionLocation);
    }

    public void bindColor(int aColorLocation){
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.mColorBuffer);
        glEnableVertexAttribArray(aColorLocation);
    }

    public void bindSize(int aSizeLocation){
        glVertexAttribPointer(aSizeLocation, SIZE_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.mSizeBuffer);
        glEnableVertexAttribArray(aSizeLocation);
    }

    public float[] getCentroid() {
        float[] centroid = {0, 0, 0};
        mVertexBuffer.rewind();
        for (int i = 0; i < getVertexCount(); i++) {
            centroid[0] += mVertexBuffer.get();
            centroid[1] += mVertexBuffer.get();
            centroid[2] += mVertexBuffer.get();
        }
        centroid[0] /= getVertexCount();
        centroid[1] /= getVertexCount();
        centroid[2] /= getVertexCount();
        mVertexBuffer.rewind();
        return centroid;
    }

    public float[] getModelMatrix() {
        MatrixHelper.multMatrices(modelMatrix,
                transMatrix,
                rotationMatrix,
                scaleMatrix,
                centerMatrix
        );
        return modelMatrix;
    }

    public void rotate(float[] angles){
        MatrixHelper.addRotationToMatrix(rotationMatrix, angles);
    }

    public void scale(float scale){
        scaleMatrix[0]= scale;
        scaleMatrix[5]= scale;
        scaleMatrix[10]= scale;
    }

    public void centerOnCentroid(){
        float[] centroid = getCentroid();
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.translateM(centerMatrix, 0, -centroid[0], -centroid[1], -centroid[2]);
    }

    public abstract void draw();

    public int getVertexCount() {
        return mVertexBuffer.capacity() / 3;
    }
}
