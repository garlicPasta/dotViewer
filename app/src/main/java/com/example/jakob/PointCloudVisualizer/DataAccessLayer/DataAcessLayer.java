package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;

public class DataAcessLayer {
    public static final int SERVER_PORT= 8080;
    public static final String SERVER_IP= "192.168.2.102:8080";

    RequestQueue queue;
    Context c;
    HashMap<String,String> cache;

    public DataAcessLayer(Context c){
        this.queue = Volley.newRequestQueue(c);
        this.c = c;
    }

    public void receiveKey(String key){
        StringRequest sR = new StringRequest(Request.Method.GET, this.SERVER_IP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response.substring(0, 500));
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error ", "Http Request didnt work");
            }
        });
        this.queue.add(sR);
    }

    public LRUCache buildLRUCache(){
        return new LRUCache(queue);
    };

    public MultiResTreeProtos buildMultiResTreeProtos() {

    }
}
