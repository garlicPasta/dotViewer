package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;


/**
 * Singleton Camera class
 */
public class CameraGL {
    float[] projectionMatrix;
    float[] viewMatrix;

    public CameraGL() {
        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -100.0f, 0f, 0f, 1f, 0f, 1.0f, 0.0f);
    }

    public CameraGL(float ratio) {
        super();
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 140);
    }

    public void updateRation(float ratio){
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 140);
    }

    public void rotate(float[] angle){
        MatrixHelper.addRotationToMatrix(viewMatrix, angle);
    }
}
