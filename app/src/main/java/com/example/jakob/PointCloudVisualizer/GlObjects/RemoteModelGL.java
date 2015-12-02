package com.example.jakob.PointCloudVisualizer.GlObjects;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.LRUCache;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_POINTS;

public class RemoteModelGL extends ModelGL{
    LRUCache dataAcces;

    public RemoteModelGL(LRUCache cache) {
        super(cache.vertexBuffer, cache.colorBuffer);
        dataAcces = cache;
        vertexCount = 0;
    }

    public void fetchData(){
        for (int i=0; i< 24000; i++){
            dataAcces.get(Integer.toString(i));
            vertexCount+= 100;
        }
        centerOnCentroid();
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0 , vertexCount);
    }
}
