package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class LRUCache {

    static int POINT_COUNT = 1000;
    static int BLOCK_COUNT = 400;
    public static int MAX_POINTS = POINT_COUNT * BLOCK_COUNT;
    static int FLOAT_SIZE = 4;
    static int BLOCK_SIZE = POINT_COUNT * FLOAT_SIZE * 3;
    static int BUFFER_SIZE = BLOCK_SIZE * BLOCK_COUNT;

    private final RequestQueue queue;

    public FloatBuffer vertexBuffer;
    public FloatBuffer colorBuffer;
    public FloatBuffer sizeBuffer;
    public int vertexCount;

    HashMap<String, CacheNode> map = new HashMap<>();
    CacheNode head=null;
    CacheNode end=null;

    public LRUCache(RequestQueue queue) {
        this.queue = queue;
        createFloatBuffers();
        for (int i=0; i < BLOCK_COUNT; i++){
            setHead(new CacheNode(Integer.toString(i) + "foo",
                    new float[3000], new float[3000], new float[1000],
                    vertexBuffer, colorBuffer, sizeBuffer, i * POINT_COUNT * 3));
        }

        for (int i=0; i <= BLOCK_COUNT *2; i++){
            updateCache(Integer.toString(i) + "bar",
                    new float[POINT_COUNT * 3], new float[POINT_COUNT * 3], new float[POINT_COUNT]);
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


    private void receiveNode(final String id) {
        final PointRequest sR = new PointRequest(
                Request.Method.GET,
                QueryFactory.buildSampleQuery(id).toString(),
                this,
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Volley ","Error for request");
                }
        }, id);
        this.queue.add(sR);
    }

    public void rewindAllBuffer(){
        this.vertexBuffer.rewind();
        this.colorBuffer.rewind();
        this.sizeBuffer.rewind();
    }
}
