package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

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
        xyzrgbsBuffer.position(0);
        glEnableVertexAttribArray(aPositionLocation);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, stride, this.xyzrgbsBuffer);
    }

    public void bindColor(int aColorLocation){
        xyzrgbsBuffer.position(POSITION_COMPONENT_COUNT);
        glEnableVertexAttribArray(aColorLocation);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, stride, this.xyzrgbsBuffer);
    }

    public void bindSize(int aSizeLocation){
        xyzrgbsBuffer.position(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT);
        glEnableVertexAttribArray(aSizeLocation);
        glVertexAttribPointer(aSizeLocation, SIZE_COMPONENT_COUNT, GL_FLOAT,
                false, stride, this.xyzrgbsBuffer);
    }

    public void draw(){
        glDrawArrays(GL_POINTS, 0, pointCount);
    }

    public void setXyzrgbsBuffer(FloatBuffer xyzrgbsBuffer) {
        this.xyzrgbsBuffer = xyzrgbsBuffer;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public boolean isDrawable(){
        return xyzrgbsBuffer != null;
    }
}
