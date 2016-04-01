package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;

public abstract class ModelGl {

    protected float[] modelMatrix;
    protected float[] transMatrix;
    protected float[] rotationMatrix;
    protected float[] scaleMatrix;
    protected float[] centerMatrix;

    protected CameraGL camera;
    protected float scale;

    public int aPositionLocation;
    public int aColorLocation;
    public int aSizeLocation;

    ModelGl(){
        scale = 1;
        initMatrices();
    }

    private void initMatrices(){
        scaleMatrix = new float[16];
        modelMatrix = new float[16];
        transMatrix = new float[16];
        centerMatrix = new float[16];
        rotationMatrix = new float[16];
        Matrix.setIdentityM(transMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);
    }

    public float[] getModelMatrix() {
        MatrixHelper.multMatrices(modelMatrix,
                transMatrix,
                rotationMatrix,
                scaleMatrix,
                centerMatrix
        );
        return modelMatrix;
    }

    public void rotate(float[] angles){
        MatrixHelper.addRotationToMatrix(rotationMatrix, angles);
    }

    public void scale(float scale){
        scaleMatrix[0]= scale;
        scaleMatrix[5]= scale;
        scaleMatrix[10]= scale;
    }

    public void setCamera(CameraGL camera) {
        this.camera = camera;
    }

    public abstract void draw();
}
