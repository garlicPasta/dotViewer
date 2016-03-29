package com.example.jakob.PointCloudVisualizer.DataAccessLayer;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.jakob.PointCloudVisualizer.GlObjects.MultiResolutionTreeGLOwner;


public class DataAccessLayer {
    public static final String SERVER_IP = "192.168.2.104:8080";

    RequestQueue queue;

    public DataAccessLayer(Context c){
        this.queue = Volley.newRequestQueue(c);
    }

    /*
    public LRUCache buildLRUCache(){
        return new LRUCache(queue);
    };
    */

    public void buildMultiResTreeProtos(MultiResolutionTreeGLOwner owner) {
        MRTRequest.sendRequest(queue, owner);
    }

    public void getSamples(String id,LRUDrawableCache cache){
        SampleRequest.sendRequest(id, queue, cache);
    }
}
