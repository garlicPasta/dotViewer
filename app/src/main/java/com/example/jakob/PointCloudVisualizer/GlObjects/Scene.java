package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;
import java.util.LinkedList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;
import static com.example.jakob.PointCloudVisualizer.GlObjects.FactoryModels.buildPlane;


public class Scene {
    List<ModelGL> sceneModels;
    RemotePointClusterGL pointCluster;
    OctreeWireGL octree;
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

    public void drawScene(int aPositionLocation, int aColorLocation,
                          int aSizeLocation, int uMVPMatrixLocation){
        drawBackground(aPositionLocation, aColorLocation, uMVPMatrixLocation);
        for (int i = 0; i < sceneModels.size(); i++) {
            ModelGL model = sceneModels.get(i);
            Matrix.setIdentityM(mvpMatrix, 0);
            MatrixHelper.multMatrices(
                    mvpMatrix,
                    camera.projectionMatrix,
                    camera.viewMatrix,
                    model.getModelMatrix(),
                    model.centerMatrix);
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
            model.bindVertex(aPositionLocation);
            model.bindColor(aColorLocation);
            model.bindSize(aSizeLocation);
            model.draw();
        }
    }

    public synchronized void addModel(ModelGL models){
        sceneModels.add(models);
    }

    public void addModels(List<OctreeWireGL.BoxGL> models){
        for (ModelGL model : models){
            addModel(model);
        }
    }

    public void setCamera(CameraGL camera){
        this.camera = camera;
    }

    public void setupBackground(){
        background = buildPlane();
        background.scale(1);
    }

    public void drawBackground(int aPositionLocation, int aColorLocation, int uMVPMatrixLocation){
        Matrix.setIdentityM(mvpMatrix, 0);
        MatrixHelper.multMatrices(
                mvpMatrix,
                background.getModelMatrix());
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        background.bindVertex(aPositionLocation);
        background.bindColor(aColorLocation);
        background.draw();
    }

    public void rotateScene(float[] angles){
        for (ModelGL m : sceneModels)
            m.rotate(angles);
    }

    public void scaleScene(float scale){
        for (ModelGL m : sceneModels)
            m.scale(scale);
    }

    public void setPointCluster(RemotePointClusterGL pointCluster) {
        this.pointCluster = pointCluster;
        addModel(pointCluster);
    }

    public void setOctree(OctreeWireGL octree) {
        this.octree = octree;
        for (String id : octree.getAllIds()){
                //pointCluster.fetchData(id);
        }
        pointCluster.fetchData(octree.root.id);
    }
}
