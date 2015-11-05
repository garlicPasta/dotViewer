package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.GLES20;

import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;

import java.util.LinkedList;
import java.util.List;

public class Scene {
    List<ModelGL> sceneModels;
    CameraGL camera;

    public Scene(){
        sceneModels = new LinkedList<>();
    }

    public void drawScene(int aPositionLocation, int aColorLocation, int uMVPMatrixLocation){
        float[] mvpMatrix = new float[16];
        for (ModelGL model : sceneModels){
            MatrixHelper.multMatrices(
                    mvpMatrix,
                    camera.projectionMatrix,
                    camera.viewMatrix,
                    model.getModelMatrix());
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false,mvpMatrix, 0);
            model.bindVertex(aPositionLocation);
            model.bindColor(aColorLocation);
            model.draw();
        }
    }

    public void addModel(ModelGL modelGL){
        sceneModels.add(modelGL);
    }

    public void setCamera(CameraGL camera){
        this.camera = camera;
    }

    public void rotateScene(float[] angles){
        //this.camera.rotate(angles);
        sceneModels.get(0).rotate(angles);
    }

    public void scaleScene(float scale){
        sceneModels.get(0).scale(scale);
    }
}
