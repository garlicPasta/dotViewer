package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUDrawableCache {

    static int POINT_COUNT = 100000;
    static int BLOCK_COUNT = 1;
    public static int MAX_POINTS = POINT_COUNT * BLOCK_COUNT;

    private int currentPointCount;

    Map<String, DrawableBufferNode> map = new ConcurrentHashMap<>();
    List<String> activeNodes;
    DrawableBufferNode head=null;
    DrawableBufferNode end=null;

    public LRUDrawableCache() {}

    public boolean containsSample(String id){
        return map.containsKey(id);
    }

    public synchronized void set(DrawableBufferNode node) {
        if(map.containsKey(node.key)) {
            DrawableBufferNode old = map.get(node.key);
            remove(old);
        }
        while (currentPointCount + (node.pointCount) > MAX_POINTS) {
            remove(end);
        }
        setHead(node);
    }

    protected synchronized void remove(DrawableBufferNode n){
        if (n.isVBO)
            n.releaseVBO();
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

    public void drawAll(int aPositionLocation, int aColorLocation, int aSizeLocation) {
        for (DrawableBufferNode node : map.values()){
            drawNode(node, aPositionLocation, aColorLocation, aSizeLocation);
        }
    }

    public void drawIds(int aPositionLocation, int aColorLocation, int aSizeLocation,
                        List<String> ids) {
        for (String id : ids){
            drawNode(map.get(id), aPositionLocation, aColorLocation, aSizeLocation);
        }
    }

    public void drawActiveNodes(int aPositionLocation, int aColorLocation, int aSizeLocation){
        if (activeNodes != null)
            drawIds(aPositionLocation, aColorLocation, aSizeLocation, activeNodes);
    }

    private void drawNode(DrawableBufferNode node, int aPositionLocation, int aColorLocation,
                          int aSizeLocation){
        if (node != null && node.isVBO()){
            node.bindVertex(aPositionLocation);
            node.bindColor(aColorLocation);
            node.bindSize(aSizeLocation);
            node.draw();
        }

    }

    public DrawableBufferNode getNode(String id){
        return map.get(id);
    }

    public void updatePointCount(int points){
        while (currentPointCount + points > MAX_POINTS){
            remove(end);
        }
        currentPointCount =+ points;
    }

    public void setActiveNodes(List<String> activeNodes) {
        this.activeNodes = activeNodes;
    }
}
