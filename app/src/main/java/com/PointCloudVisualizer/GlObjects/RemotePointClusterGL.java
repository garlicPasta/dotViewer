package com.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;

import com.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
import com.PointCloudVisualizer.DataAccessLayer.DrawableBufferNode;
import com.PointCloudVisualizer.DataAccessLayer.LRUDrawableCache;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RemotePointClusterGL extends ModelGl implements MultiResolutionTreeGLOwner{
    private MultiResolutionTreeGL mrt;
    private DataAccessLayer dal;
    private LRUDrawableCache cache;
    private ThreadPoolExecutor executor;


    public RemotePointClusterGL(DataAccessLayer d){
        super();
        dal = d;
        cache = new LRUDrawableCache();
        executor = new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>());
        d.buildMultiResTreeProtos(this);
    }

    public void updateCache(){
        executor.getQueue().clear();
        if (mrt != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<String> activeIds = mrt.getIdsViewDependent();
                    cache.setActiveNodes(activeIds);
                    for (String id : activeIds) {
                        if (!cache.containsSample(id)){
                            if(executor.getQueue().size() > 0)
                                return;
                            cache.set(new DrawableBufferNode(id));
                            dal.getSamples(id, cache);
                        }
                    }
                }
            });
            executor.getQueue();
        }
    }

    @Override
    public void draw() {
        if (mrt != null) {
            cache.drawActiveNodes(aPositionLocation, aColorLocation, aSizeLocation);
            //drawActiveOctans();
        }

    }

    public void drawActiveOctans(){
        for (BoxGL box : mrt.exportActiceNodes()){
            box.bindVertex(aPositionLocation);
            box.bindColor(aColorLocation);
            box.bindSize(aSizeLocation);
            box.draw();
        }
    }

    @Override
    public void setMultiResolutionTree(MultiResolutionTreeGL tree) {
        mrt = tree;
    }

    @Override
    public void rotate(float[] angles){
        super.rotate(angles);
        updateCache();
    }

    @Override
    public void scale(float scale){
        super.scale(scale);
        updateCache();
    }

    @Override
    public void centerObject(){
        Matrix.setIdentityM(centerMatrix, 0);
        if (mrt != null)
            Matrix.translateM(centerMatrix,0 ,-mrt.root.center[0], -mrt.root.center[1], -mrt.root.center[2]);
    }
}
