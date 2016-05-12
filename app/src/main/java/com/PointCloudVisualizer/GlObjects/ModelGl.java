package com.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;
import com.PointCloudVisualizer.util.MatrixHelper;

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
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);
    }

    public float[] getModelMatrix() {
        MatrixHelper.multMatrices(modelMatrix, modelMatrix, centerMatrix);
        return modelMatrix;
    }

    public void rotate(float[] angles){
        MatrixHelper.addRotationToMatrix(modelMatrix, angles);
    }

    public void scale(float scale){
        scaleMatrix[0]= scale;
        scaleMatrix[5]= scale;
        scaleMatrix[10]= scale;
        Matrix.scaleM(modelMatrix, 0, scale,scale,scale);
    }

    public void translate(float[] trans){
        Matrix.translateM(modelMatrix, 0, trans[0], trans[1], trans[2]);
    }

    public void setCamera(CameraGL camera) {
        this.camera = camera;
    }

    public abstract void draw();

    public abstract void centerObject();

}
