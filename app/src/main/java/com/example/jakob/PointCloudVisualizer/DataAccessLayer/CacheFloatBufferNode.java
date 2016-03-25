package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import com.example.jakob.PointCloudVisualizer.util.BufferHelper;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.GLRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.GLRender.POSITION_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.GLRender.SIZE_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_POINTS;


public class CacheFloatBufferNode {

    String key;
    CacheNode pre;
    CacheNode next;
    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    FloatBuffer sizeBuffer;
    int pointCount;


    public CacheFloatBufferNode(String key, float[] vertices, float[] colors, float[] size){
        this.key = key;
        pointCount = vertices.length / 3;
        vertexBuffer = BufferHelper.buildFloatBuffer(vertices);
        colorBuffer = BufferHelper.buildFloatBuffer(colors);
        sizeBuffer = BufferHelper.buildFloatBuffer(size);
    }

    public void bindVertex(int aPositionLocation){
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.vertexBuffer);
        glEnableVertexAttribArray(aPositionLocation);
    }

    public void bindColor(int aColorLocation){
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.colorBuffer);
        glEnableVertexAttribArray(aColorLocation);
    }

    public void bindSize(int aSizeLocation){
        glVertexAttribPointer(aSizeLocation, SIZE_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.sizeBuffer);
        glEnableVertexAttribArray(aSizeLocation);
    }

    public void draw(){
        glDrawArrays(GL_POINTS, 0, pointCount);
    }
}
