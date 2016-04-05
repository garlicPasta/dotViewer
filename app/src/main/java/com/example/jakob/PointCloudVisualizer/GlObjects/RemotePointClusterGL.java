package com.example.jakob.PointCloudVisualizer.GlObjects;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DrawableBufferNode;
import com.example.jakob.PointCloudVisualizer.DataAccessLayer.LRUDrawableCache;

import java.util.List;


public class RemotePointClusterGL extends ModelGl implements MultiResolutionTreeGLOwner{
    private MultiResolutionTreeGL mrt;
    private DataAccessLayer dal;
    private LRUDrawableCache cache;


    public RemotePointClusterGL(DataAccessLayer d){
        super();
        dal = d;
        cache = new LRUDrawableCache();
        d.buildMultiResTreeProtos(this);
    }

    public void updateCache(CameraGL camera){
        if (mrt != null) {
            List<String> activeIds = mrt.getIdsViewDependent();
            cache.setActiveNodes(activeIds);
            for (String id : activeIds) {
                if (!cache.containsSample(id)){
                    cache.set(new DrawableBufferNode(id));
                    dal.getSamples(id, cache);
                }
            }
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
        updateCache(null);
    }

    @Override
    public void scale(float scale){
        super.scale(scale);
        updateCache(null);
    }
}
