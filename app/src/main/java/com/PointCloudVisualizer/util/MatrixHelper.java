package com.PointCloudVisualizer.util;

import android.opengl.Matrix;

public class MatrixHelper {

    public static void multMatrices(float[] produktMatrix, float[]... matrices){
        float[] tmp= new float[16];
        Matrix.setIdentityM(tmp, 0);
        for (int i= matrices.length - 1; i>=0; i--){
            Matrix.multiplyMM(tmp, 0, matrices[i], 0,tmp, 0);
        }
        for (int i=0; i< 16; i++){
           produktMatrix[i]  = tmp[i];
        }
    }

    public static float[] addRotationToMatrix(float rotX, float rotY, float rotZ){
        float[] mRotMatrixX = new float[16];
        float[] mRotMatrixY = new float[16];
        float[] mRotMatrix =  new float[16];

        Matrix.setRotateM(mRotMatrixX, 0, rotY, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(mRotMatrixY, 0, rotZ, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mRotMatrix, 0, mRotMatrixX, 0, mRotMatrixY, 0);
        return mRotMatrix;
    }

    public static void addRotationToMatrix(float[] rotMat, float[] rotation){
        float[] mRotMatrixX = new float[16];
        float[] mRotMatrixY = new float[16];
        float[] mRotMatrixZ = new float[16];

        Matrix.setRotateM(mRotMatrixX, 0, rotation[0], 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(mRotMatrixY, 0, rotation[1], 0.0f, 1.0f, 0.0f);
        Matrix.setRotateM(mRotMatrixZ, 0, rotation[2], 0.0f, 0.0f, 1.0f);
        multMatrices(rotMat, mRotMatrixX, mRotMatrixY, mRotMatrixZ, rotMat);
    }
}
