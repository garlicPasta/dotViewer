package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import java.nio.FloatBuffer;
import java.util.HashMap;

public class LRUCache {

    static int POINT_COUNT= 32;
    static int BLOCK_COUNT= 1024;
    static int FLOAT_SIZE= 4;
    static int BLOCK_SIZE= POINT_COUNT * FLOAT_SIZE * 3;
    static int BUFFER_SIZE= BLOCK_SIZE * BLOCK_COUNT;

    int capacity;
    int bufferPointer;

    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;

    HashMap<Integer, CacheNode> map = new HashMap<>();
    CacheNode head=null;
    CacheNode end=null;

    public LRUCache() {
        this.capacity = BLOCK_COUNT;
        for (int i=0; i < capacity; i++){
            set(i ,null, i* BLOCK_SIZE);
        }
    }

    public void get(int key) {
        if(map.containsKey(key)){
            CacheNode n = map.get(key);
            remove(n);
            setHead(n);
        }

        float[] data = null;
        set(key, data, end.offset);
    }

    public void set(int key, float[] data, int offset) {
        if(map.containsKey(key)) {
            CacheNode old = map.get(key);
            remove(old);
        }
        CacheNode created = new CacheNode(key, data , offset, vertexBuffer, colorBuffer );
        map.remove(end.key);
        remove(end);
        setHead(created);
        map.put(key, created);
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
}
