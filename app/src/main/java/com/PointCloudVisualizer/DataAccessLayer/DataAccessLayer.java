package com.PointCloudVisualizer.DataAccessLayer;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.PointCloudVisualizer.GlObjects.MultiResolutionTreeGLOwner;

import java.security.InvalidKeyException;


public class DataAccessLayer {
    public String serverUrl;

    public RequestQueue queue;

    public DataAccessLayer(Context c){
        this.queue = Volley.newRequestQueue(c);
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        try {
            serverUrl = p.getString("serverIP", "InvalidKey");
            if (serverUrl.equals("InvalidKey")){
            throw new InvalidKeyException("No IP address specified for server");
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void buildMultiResTreeProtos(MultiResolutionTreeGLOwner owner) {
        MRTRequest.sendRequest(serverUrl, queue, owner);
    }

    public void getSamples(String id,LRUDrawableCache cache){
        SampleRequest.sendRequest(serverUrl, id, queue, cache);
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
