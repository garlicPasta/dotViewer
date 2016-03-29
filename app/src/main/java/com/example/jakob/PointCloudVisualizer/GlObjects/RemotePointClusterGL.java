package com.example.jakob.PointCloudVisualizer.GlObjects;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.DataAccessLayer;
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
        for (String id : mrt.getIdsMaxLevel(5)){
            if (!cache.containsSample(id))
                dal.getSamples(id, cache);
        }
    }

    @Override
    public void draw() {
        cache.draw(aPositionLocation, aColorLocation, aSizeLocation);
    }

    @Override
    public void setMultiResolutionTree(MultiResolutionTreeGL tree) {
        mrt = tree;
        updateCache(null);
    }
}
