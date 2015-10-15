package com.example.jakob.PointCloudVisualizer;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_POINTS;


public class PointModelGL extends ModelGL{


    PointModelGL(float[] vertices) {
        super(vertices);
    }

    PointModelGL(float[] vertices, float[] colors) {
        super(vertices, colors);
    }

    PointModelGL(FloatBuffer vertices, FloatBuffer colors) {
        super(vertices, colors);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0 , size);
    }
}
