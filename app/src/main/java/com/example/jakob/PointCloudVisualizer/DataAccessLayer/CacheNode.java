package com.example.jakob.PointCloudVisualizer.DataAccessLayer;


import java.nio.FloatBuffer;

public class CacheNode {
        String key;
        int offset;
        CacheNode pre;
        CacheNode next;

        public CacheNode(String key, float[] vertices, float[] colors, int offset, FloatBuffer vertexBuffer,
                         FloatBuffer colorBuffer){
            this.key = key;
            this.offset= offset;
            vertexBuffer.position(offset);
            vertexBuffer.put(vertices);
            colorBuffer.put(colors);
        }
}
