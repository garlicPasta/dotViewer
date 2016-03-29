package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUDrawableCache {

    static int POINT_COUNT = 3700;
    static int BLOCK_COUNT = 267;
    public static int MAX_POINTS = POINT_COUNT * BLOCK_COUNT;

    private int currentPointCount;

    Map<String, DrawableBufferNode> map = new ConcurrentHashMap<>();
    DrawableBufferNode head=null;
    DrawableBufferNode end=null;

    public LRUDrawableCache() {}

    public boolean containsSample(String id){
        return map.containsKey(id);
    }

    public synchronized void set(String key, FloatBuffer vertices, FloatBuffer colors,
                                 FloatBuffer size) {
        if(map.containsKey(key)) {
            DrawableBufferNode old = map.get(key);
            remove(old);
        }
        if (currentPointCount + (vertices.capacity() / 3) < MAX_POINTS) {
            DrawableBufferNode created = new DrawableBufferNode(key, vertices, colors, size);
            setHead(created);
        } else {
            remove(end);
        }
    }

    private synchronized void remove(DrawableBufferNode n){
        if(n.pre!=null)
            n.pre.next = n.next;
        else
            head = n.next;

        if (n.next!=null)
            n.next.pre = n.pre;
        else
            end = n.pre;
        currentPointCount -= n.pointCount;
        map.remove(n.key);
    }

    private synchronized void setHead(DrawableBufferNode n){
        n.next = head;
        n.pre = null;

        if(head != null)
            head.pre = n;
        head = n;

        if(end == null)
            end = head;

        currentPointCount += n.pointCount;
        map.put(n.key, n);
    }

    public void draw(int aPositionLocation, int aColorLocation, int aSizeLocation) {
        for (DrawableBufferNode node : map.values()){
                node.bindVertex(aPositionLocation);
                node.bindColor(aColorLocation);
                node.bindSize(aSizeLocation);
                node.draw();
        }
    }
}
