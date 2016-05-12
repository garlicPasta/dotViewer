package com.PointCloudVisualizer.GlObjects;


import static android.opengl.GLES20.glDrawElements;
import static com.PointCloudVisualizer.util.BufferHelper.buildShortBuffer;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;

import com.PointCloudVisualizer.util.BufferHelper;
import java.nio.ShortBuffer;


public class BoxGL extends BufferModelGL {

    protected ShortBuffer mIndexBuffer;

    float[] cubeVertices = new float[]{
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
    };

    float[] cubeColor = new float[]{
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
            128f, 192f, 64f,
    };

    short indices[] = {
            0,1,
            1,2,
            2,3,
            3,0,
            4,5,
            5,6,
            6,7,
            7,4,
            0,4,
            1,5,
            2,6,
            3,7,
    };


    public BoxGL(float[] center, float length){
        super(BufferHelper.buildFloatBuffer(24));
        for (int i = 0; i < cubeVertices.length / 3 ; i++) {
            for (int j = 0; j < 3; j++) {
                cubeVertices[3 * i + j] *= length;
                cubeVertices[3 * i + j] += center[j];
            }
        }
        this.mVertexBuffer.put(cubeVertices);
        this.mVertexBuffer.position(0);
        this.mColorBuffer.put(cubeColor);
        this.mColorBuffer.position(0);
        this.mIndexBuffer = buildShortBuffer(indices);
    }

    @Override
    public void draw(){
        glDrawElements(GL_LINES, indices.length, GL_UNSIGNED_SHORT, mIndexBuffer);
    }
    @Override
    public void centerObject(){}
}

