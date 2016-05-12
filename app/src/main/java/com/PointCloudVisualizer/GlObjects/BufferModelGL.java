package com.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;

import com.PointCloudVisualizer.util.BufferHelper;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.PointCloudVisualizer.GLRender.COLOR_COMPONENT_COUNT;
import static com.PointCloudVisualizer.GLRender.POSITION_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static com.PointCloudVisualizer.GLRender.SIZE_COMPONENT_COUNT;
import static com.PointCloudVisualizer.util.BufferHelper.buildFloatBuffer;

public abstract class BufferModelGL extends ModelGl{

    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mColorBuffer;
    protected FloatBuffer mSizeBuffer;


    BufferModelGL(FloatBuffer vertices){
        super();
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
    }

    BufferModelGL(FloatBuffer vertices, FloatBuffer colors) {
        this(vertices);
        mColorBuffer = colors;
        mColorBuffer.position(0);
    }

    BufferModelGL(FloatBuffer vertices, FloatBuffer colors, FloatBuffer size) {
        this(vertices, colors);
        mSizeBuffer = size;
        mSizeBuffer.position(0);
    }

    BufferModelGL(float[] vertices) {
        this(buildFloatBuffer(vertices, 3));
    }

    BufferModelGL(float[] vertices, float[] colors) {
        this(buildFloatBuffer(vertices, 4), buildFloatBuffer(colors, 4));
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

    public void centerOnCentroid(){
        float[] centroid = getCentroid();
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.translateM(centerMatrix, 0, -centroid[0], -centroid[1], -centroid[2]);
    }


    public int getVertexCount() {
        return mVertexBuffer.capacity() / 3;
    }
}
