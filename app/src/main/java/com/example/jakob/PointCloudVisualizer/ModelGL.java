package com.example.jakob.PointCloudVisualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.POSITION_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;

public abstract class ModelGL {
    protected FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    protected int size;

    ModelGL(float[] vertices) {
        size = vertices.length / 3;
        mVertexBuffer = buildFloatBuffer(vertices, 4);
    }

    ModelGL(float[] vertices, float[] colors) {
        mVertexBuffer = buildFloatBuffer(vertices, 4);
        mColorBuffer = buildFloatBuffer(colors, 4);
        size = vertices.length / 3;
    }

    ModelGL(FloatBuffer vertices, FloatBuffer colors) {
        mVertexBuffer = vertices;
        mVertexBuffer.position(0);
        mColorBuffer = colors;
        mColorBuffer.position(0);
        size = vertices.capacity() / 3;
    }

    private FloatBuffer buildFloatBuffer(float[] array, int typeSize){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(array.length * typeSize);
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = byteBuf.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
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

    public int getSize() {
        return size;
    }

    public float[] getCentroid() {
        float[] centroid = {0, 0, 0};
        mVertexBuffer.rewind();
        while (mVertexBuffer.hasRemaining()) {
            centroid[0] += mVertexBuffer.get();
            centroid[1] += mVertexBuffer.get();
            centroid[2] += mVertexBuffer.get();
        }
        centroid[0] /= size;
        centroid[1] /= size;
        centroid[2] /= size;
        mVertexBuffer.rewind();
        return centroid;
    }

}
