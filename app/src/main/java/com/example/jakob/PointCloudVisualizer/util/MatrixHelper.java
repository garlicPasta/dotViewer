package com.example.jakob.PointCloudVisualizer.util;

import android.opengl.Matrix;

public class MatrixHelper {

    public static float[] multMatrices(float[]... matrices){
        if (matrices.length <2 ){
            throw new IllegalArgumentException("At least two matrcies have to be given");
        }
        float[] temp = new float[16];
        Matrix.setIdentityM(temp, 0);
        for (int i= matrices.length - 1; i>=0; i--){
            Matrix.multiplyMM(temp, 0, matrices[i], 0, temp, 0);
        }
        return temp;
    }
}
