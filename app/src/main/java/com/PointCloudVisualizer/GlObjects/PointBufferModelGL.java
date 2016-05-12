package com.PointCloudVisualizer.GlObjects;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_POINTS;


public class PointBufferModelGL extends BufferModelGL {


    public PointBufferModelGL(float[] vertices) {
        super(vertices);
    }

    public PointBufferModelGL(float[] vertices, float[] colors) {
        super(vertices, colors);
    }

    public PointBufferModelGL(FloatBuffer vertices, FloatBuffer colors) {
        super(vertices, colors);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0 , getVertexCount());
    }

    @Override
    public void centerObject(){}
}
