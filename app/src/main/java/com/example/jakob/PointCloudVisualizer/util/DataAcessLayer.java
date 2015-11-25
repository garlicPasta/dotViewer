package com.example.jakob.PointCloudVisualizer.util;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpClientConnection;

public class DataAcessLayer {
    final int SERVER_PORT= 8080;
    final String SERVER_IP= "http://192.168.2.102:8080";
    RequestQueue queue;

    public DataAcessLayer(Context c){
        this.queue = Volley.newRequestQueue(c);
    }

    public void receiveKey(String key){
        StringRequest sR = new StringRequest(Request.Method.GET, this.SERVER_IP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response.substring(0, 2));
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error ", "Http Request didnt work");
            }
        });
        this.queue.add(sR);
    }
}
