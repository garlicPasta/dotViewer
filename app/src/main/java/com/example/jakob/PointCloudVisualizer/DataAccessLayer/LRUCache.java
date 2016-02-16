package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class LRUCache {

    static int POINT_COUNT= 1000;
    static int BLOCK_COUNT= 10;
    static int FLOAT_SIZE= 4;
    static int BLOCK_SIZE= POINT_COUNT * FLOAT_SIZE * 3;
    static int BUFFER_SIZE= BLOCK_SIZE * BLOCK_COUNT;

    static String SERVER_IP= "192.168.2.102:8080";
    final int SERVER_PORT = 8080;
    private final RequestQueue queue;

    int capacity;

    public FloatBuffer vertexBuffer;
    public FloatBuffer colorBuffer;
    public FloatBuffer sizeBuffer;

    HashMap<String, CacheNode> map = new HashMap<>();
    CacheNode head=null;
    CacheNode end=null;

    public LRUCache(Context context) {
        this.capacity = BLOCK_COUNT;
        this.queue = Volley.newRequestQueue(context);
        createFloatBuffers();
        for (int i=0; i < capacity; i++){
            setHead(new CacheNode(Integer.toString(i) + "foo",
                    new float[3], new float[3], new float[3],
                    vertexBuffer, colorBuffer, sizeBuffer, i * POINT_COUNT * 3));
        }
    }

    public void get(String key) {
        if(map.containsKey(key)){
            CacheNode n = map.get(key);
            remove(n);
            setHead(n);
        }
        receiveNode(key);
    }

    public void set(String key, float[] vertices, float[] colors, float[] size, int offset) {
        if(map.containsKey(key)) {
            CacheNode old = map.get(key);
            remove(old);
        }
        CacheNode created = new CacheNode(key, vertices, colors, size,
                 vertexBuffer, colorBuffer, sizeBuffer, offset);
        map.remove(end.key);
        remove(end);
        setHead(created);
        map.put(key, created);
    }

    public synchronized void updateCache(String key, float[] vertices, float[] colors, float[] size){
        set(key, vertices, colors, size, end.offset);
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

        byteBuf = ByteBuffer.allocateDirect(BUFFER_SIZE / 3);
        byteBuf.order(ByteOrder.nativeOrder());
        sizeBuffer = byteBuf.asFloatBuffer();
        sizeBuffer.position(0);
    }

    private class PointRequest extends StringRequest{
        private String key;

        public PointRequest(int method, String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener, String key) {
            super(method, url, listener, errorListener);
            this.key = key;
        }

        @Override
        protected Response parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            BufferedReader bufReader = new BufferedReader(new StringReader(parsed));
            String line;

            float[] vertices = new float[3 * POINT_COUNT];
            float[] colors = new float[3 * POINT_COUNT];
            float[] size = new float[POINT_COUNT];

            int i = 0;
            int j = 0;
            int k = 0;

            try {
                while( (line=bufReader.readLine()) != null ) {
                    String[] responseArray = line.split(" ");
                    vertices[i++] = Float.parseFloat(responseArray[0]);
                    vertices[i++] = Float.parseFloat(responseArray[1]);
                    vertices[i++] = Float.parseFloat(responseArray[2]);
                    colors[j++] = Float.parseFloat(responseArray[3]);
                    colors[j++] = Float.parseFloat(responseArray[4]);
                    colors[j++] = Float.parseFloat(responseArray[5]);
                    size[k++] = Float.parseFloat(responseArray[6]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateCache(key, vertices, colors, size);
            //Log.d("Response", response.substring(0, 500));
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    private void receiveNode(final String query) {
        final StringRequest sR = new PointRequest(Request.Method.GET, query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley ","Receive Request for " + query);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley ","Error for request");
            }
        }, query);
        this.queue.add(sR);
    }

    public void rewindAllBuffer(){
        this.vertexBuffer.rewind();
        this.colorBuffer.rewind();
        this.sizeBuffer.rewind();
    }

}
