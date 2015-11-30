package com.example.jakob.PointCloudVisualizer.DataAccessLayer;


import java.nio.FloatBuffer;

public class CacheNode {
        int key;
        int offset;
        CacheNode pre;
        CacheNode next;

        public CacheNode(int key, float[] data, int offset, FloatBuffer vertexBuffer,
                         FloatBuffer colorBuffer){
            this.key = key;
            this.offset= offset;
            vertexBuffer.position(offset);
            vertexBuffer.put(data);
        }
}
