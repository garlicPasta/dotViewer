package com.example.jakob.PointCloudVisualizer.GlObjects;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;


public class PolyBufferModelGL extends BufferModelGL {


    PolyBufferModelGL(float[] vertices) {
        super(vertices);
    }

    PolyBufferModelGL(float[] vertices, float[] colors) {
        super(vertices, colors);
    }

    PolyBufferModelGL(FloatBuffer vertices, FloatBuffer colors) {
        super(vertices, colors);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLES, 0 , getVertexCount());
    }

}
