package com.example.jakob.PointCloudVisualizer.DataAccessLayer;


import java.nio.FloatBuffer;

public class CacheNode {
        String key;
        int offset;
        CacheNode pre;
        CacheNode next;

        public CacheNode(String key, float[] vertices, float[] colors, float[] size,
                         FloatBuffer vertexBuffer, FloatBuffer colorBuffer, FloatBuffer sizeBuffer,
                         int offset){
            this.key = key;
            this.offset= offset;
            vertexBuffer.position(offset);
            vertexBuffer.put(vertices);
            colorBuffer.position(offset);
            colorBuffer.put(colors);
            sizeBuffer.position(offset / 3);
            sizeBuffer.put(size);
            vertexBuffer.rewind();
            colorBuffer.rewind();
            sizeBuffer.rewind();
        }
}
