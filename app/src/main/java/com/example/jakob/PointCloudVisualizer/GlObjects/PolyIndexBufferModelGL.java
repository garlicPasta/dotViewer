package com.example.jakob.PointCloudVisualizer.GlObjects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_TRIANGLES;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildByteBuffer;

public class PolyIndexBufferModelGL extends PolyBufferModelGL {

    ByteBuffer indices;

    PolyIndexBufferModelGL(float[] vertices, byte[] indices) {
        super(vertices);
        this.indices = buildByteBuffer(indices);
    }

    PolyIndexBufferModelGL(float[] vertices, float[] colors, byte[] indices) {
        super(vertices, colors);
        this.indices = buildByteBuffer(indices);
    }

    PolyIndexBufferModelGL(FloatBuffer vertices, FloatBuffer colors, byte[] indices) {
        super(vertices, colors);
        this.indices = buildByteBuffer(indices);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_BYTE, indices);
    }
}
