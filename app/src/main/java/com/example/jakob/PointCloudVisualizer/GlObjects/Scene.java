package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;
import java.util.LinkedList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static com.example.jakob.PointCloudVisualizer.GlObjects.FactoryModels.buildPlane;

public class Scene {
    List<ModelGL> sceneModels;
    PolyModelGL background;
    CameraGL camera;
    GL10 gl;

    float[] mvpMatrix;

    public Scene(GL10 gl){
        this.gl = gl;
        sceneModels = new LinkedList<>();
        setupBackground();
        mvpMatrix = new float[16];
    }

    public void drawScene(int aPositionLocation, int aColorLocation, int uMVPMatrixLocation){
        drawBackground(aPositionLocation, aColorLocation, uMVPMatrixLocation);
        Matrix.setIdentityM(mvpMatrix, 0);
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

    public void setupBackground(){
        background = buildPlane();
        background.scale(1);

    }
    public void drawBackground(int aPositionLocation, int aColorLocation, int uMVPMatrixLocation){
        gl.glDisable(GL_DEPTH_TEST);
        Matrix.setIdentityM(mvpMatrix, 0);
        MatrixHelper.multMatrices(
                mvpMatrix,
                background.getModelMatrix());
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        background.bindVertex(aPositionLocation);
        background.bindColor(aColorLocation);
        background.draw();
        gl.glEnable(GL_DEPTH_TEST);
    }

    public void rotateScene(float[] angles){
        //this.camera.rotate(angles);
        sceneModels.get(0).rotate(angles);
    }

    public void scaleScene(float scale){
        sceneModels.get(0).scale(scale);
    }
}
