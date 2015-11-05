package com.example.jakob.PointCloudVisualizer.GlObjects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_TRIANGLES;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildByteBuffer;

public class PolyIndexModelGL extends PolyModelGL{

    ByteBuffer indices;

    PolyIndexModelGL(float[] vertices, byte[] indices) {
        super(vertices);
        this.indices = buildByteBuffer(indices);
    }

    PolyIndexModelGL(float[] vertices, float[] colors, byte[] indices) {
        super(vertices, colors);
        this.indices = buildByteBuffer(indices);;
    }

    PolyIndexModelGL(FloatBuffer vertices, FloatBuffer colors, byte[] indices) {
        super(vertices, colors);
        this.indices = buildByteBuffer(indices);;
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indices);
    }
}
