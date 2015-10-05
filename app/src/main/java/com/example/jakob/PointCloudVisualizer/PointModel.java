package com.example.jakob.PointCloudVisualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.POSITION_COMPONENT_COUNT;


public class PointModel {

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private float[] color;
    private int size;

    PointModel(float[] vertices) {
        size = vertices.length / 3;
        mVertexBuffer = buildFloatBuffer(vertices, 4);
    }

    PointModel(float[] vertices, float[] colors) {
        mVertexBuffer = buildFloatBuffer(vertices, 4);
        mColorBuffer = buildFloatBuffer(colors, 4);
        size = vertices.length / 3;
    }

    PointModel(FloatBuffer vertices, FloatBuffer colors) {
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

    public void draw() {
        glDrawArrays(GL_POINTS, 0 , size);
    }

    public int getSize() {
        return size;
    }
}
