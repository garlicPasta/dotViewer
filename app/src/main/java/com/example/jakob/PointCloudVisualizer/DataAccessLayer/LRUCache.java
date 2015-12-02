package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class LRUCache {

    static int POINT_COUNT= 100;
    static int BLOCK_COUNT= 24000;
    static int FLOAT_SIZE= 4;
    static int BLOCK_SIZE= POINT_COUNT * FLOAT_SIZE * 3;
    static int BUFFER_SIZE= BLOCK_SIZE * (BLOCK_COUNT+1);

    static String SERVER_IP= "http://192.168.2.102:8080/";
    final int SERVER_PORT = 8080;
    private final RequestQueue queue;

    int capacity;

    public FloatBuffer vertexBuffer;
    public FloatBuffer colorBuffer;

    HashMap<String, CacheNode> map = new HashMap<>();
    CacheNode head=null;
    CacheNode end=null;

    public LRUCache(Context context) {
        this.capacity = BLOCK_COUNT;
        this.queue = Volley.newRequestQueue(context);
        createFloatBuffers();
        for (int i=0; i < capacity; i++){
            setHead(new CacheNode(Integer.toString(i) + "foo", new float[3], new float[3],
                    i * POINT_COUNT * 3, vertexBuffer, colorBuffer));
        }
    }

    public void get(String key) {
        if(map.containsKey(key)){
            CacheNode n = map.get(key);
            remove(n);
            setHead(n);
        }
        receiveKey(key);
    }

    public void set(String key, float[] vertices, float[] colors, int offset) {
        if(map.containsKey(key)) {
            CacheNode old = map.get(key);
            remove(old);
        }
        CacheNode created = new CacheNode(key, vertices, colors,
                offset, vertexBuffer, colorBuffer);
        map.remove(end.key);
        remove(end);
        setHead(created);
        map.put(key, created);
    }

    public synchronized void updateCache(String key, float[] vertices, float[] colors){
        set(key, vertices, colors, end.offset);
    }

    private void remove(CacheNode n){
        if(n.pre!=null){
            n.pre.next = n.next;
        }else{
            head = n.next;
        }

        if(n.next!=null){
            n.next.pre = n.pre;
        }else{
            end = n.pre;
        }
    }

    private void setHead(CacheNode n){
        n.next = head;
        n.pre = null;

        if(head!=null)
            head.pre = n;
        head = n;

        if(end ==null)
            end = head;
    }

    private void createFloatBuffers(){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();
        vertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(BUFFER_SIZE);
        byteBuf.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuf.asFloatBuffer();
        colorBuffer.position(0);
    }

    private void receiveKey(final String key) {
        final StringRequest sR = new StringRequest(Request.Method.GET, SERVER_IP + "?key=" + key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BufferedReader bufReader = new BufferedReader(new StringReader(response));
                        String line;

                        float[] vertices = new float[3 * POINT_COUNT];
                        float[] colors= new float[3 * POINT_COUNT];
                        int i = 0;
                        int j = 0;

                        try {
                            while( (line=bufReader.readLine()) != null ) {
                                String[] responseArray = line.split(" ");
                                vertices[i++] = Float.parseFloat(responseArray[0]);
                                vertices[i++] = Float.parseFloat(responseArray[1]);
                                vertices[i++] = Float.parseFloat(responseArray[2]);
                                colors[j++] = Float.parseFloat(responseArray[3]);
                                colors[j++] = Float.parseFloat(responseArray[4]);
                                colors[j++] = Float.parseFloat(responseArray[5]);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateCache(key, vertices, colors);
                        //Log.d("Response", response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error ", "Http Request didnt work");
            }
        });
        this.queue.add(sR);
    }
}
