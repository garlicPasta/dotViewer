package com.example.jakob.PointCloudVisualizer.DataAccessLayer;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.jakob.PointCloudVisualizer.GlObjects.RemotePointClusterGLBuffer;
import com.example.jakob.PointCloudVisualizer.GlObjects.Scene;
import java.util.HashMap;


public class DataAcessLayer {
    public static final String SERVER_IP = "192.168.2.104:8080";

    RequestQueue queue;
    Context c;
    Scene scene;
    HashMap<String,String> cache;

    public DataAcessLayer(Context c){
        this.queue = Volley.newRequestQueue(c);
        this.c = c;
    }

    public LRUCache buildLRUCache(){
        return new LRUCache(queue);
    };

    public void buildMultiResTreeProtos() {
        MRTRequest.sendRequest(queue, scene);
    }

    public void buildRemotePointCluster() {
        scene.setPointCluster(new RemotePointClusterGLBuffer(buildLRUCache()));
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
