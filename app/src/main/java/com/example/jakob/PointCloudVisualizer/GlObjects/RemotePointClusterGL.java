package com.example.jakob.PointCloudVisualizer.GlObjects;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DrawableBufferNode;
import com.example.jakob.PointCloudVisualizer.DataAccessLayer.LRUDrawableCache;


public class RemotePointClusterGL extends ModelGl implements MultiResolutionTreeGLOwner{
    MultiResolutionTreeGL mrt;
    DataAccessLayer dal;
    LRUDrawableCache cache;



    public RemotePointClusterGL(DataAccessLayer d){
        super();
        dal = d;
        cache = new LRUDrawableCache();
        d.buildMultiResTreeProtos(this);
    }

    public void updateCache(CameraGL camera){
        if (mrt != null) {
            for (String id : mrt.getIdsMaxLevel(6)) {
                if (!cache.containsSample(id)){
                    cache.set(new DrawableBufferNode(id));
                    dal.getSamples(id, cache);
                }
            }
        }
    }

    @Override
    public void draw() {
        cache.draw(aPositionLocation, aColorLocation, aSizeLocation);
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
