package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;

public class CameraGL {
    float[] projectionMatrix;
    float[] viewMatrix;

    public CameraGL(float ratio) {
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -30.0f, 0f, 0f, 1f, 0f, 1.0f, 0.0f);
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 20000);
    }

    public void rotate(float[] angle){
        MatrixHelper.rotationMatrix(viewMatrix, angle);
    }
}
