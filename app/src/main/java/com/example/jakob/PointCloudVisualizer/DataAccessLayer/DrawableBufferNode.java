package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.GLRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.GLRender.POSITION_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.GLRender.SIZE_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;


public class DrawableBufferNode {

    String key;
    DrawableBufferNode pre;
    DrawableBufferNode next;
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer sizeBuffer;
    private FloatBuffer xyzrgbsBuffer;
    private int stride = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT
            + SIZE_COMPONENT_COUNT) * 4;
    public int pointCount;

    int[] buffers = new int[3];
    boolean isVBO = false;

    public DrawableBufferNode(String key){
        this.key = key;
        pointCount = 0;
    }

    public DrawableBufferNode(String key, int sampleCount){
        this.key = key;
        pointCount = sampleCount;
    }

    public DrawableBufferNode(String key, FloatBuffer vertices, FloatBuffer colors, FloatBuffer size){
        this.key = key;
        pointCount = vertices.capacity() / 3;
        vertexBuffer = vertices;
        colorBuffer = colors;
        sizeBuffer = size;
    }

    public void bindVertex(int aPositionLocation){
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, stride, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void bindColor(int aColorLocation){
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(aColorLocation);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, stride, COLOR_COMPONENT_COUNT * 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void bindSize(int aSizeLocation){
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(aSizeLocation);
        GLES20.glVertexAttribPointer(aSizeLocation, SIZE_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, stride, (COLOR_COMPONENT_COUNT + POSITION_COMPONENT_COUNT) * 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void draw(){
        glDrawArrays(GL_POINTS, 0, pointCount);
    }

    public void setXyzrgbsBuffer(FloatBuffer xyzrgbsBuffer) {
        this.xyzrgbsBuffer = xyzrgbsBuffer;
    }

    public synchronized void uploadVBO(){
        GLES20.glGenBuffers(3, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, xyzrgbsBuffer.capacity() * 4,
                xyzrgbsBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        isVBO = true;
    }

    public void releaseVBO(){
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        isVBO = false;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public boolean isVBO(){
        if (!isVBO && xyzrgbsBuffer != null){
            uploadVBO();
        }
        return isVBO;
    }
}
