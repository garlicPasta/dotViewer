package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.net.Uri;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.LRUCache;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_POINTS;

public class RemoteModelGL extends ModelGL{
    LRUCache dataAcces;

    public RemoteModelGL(LRUCache cache) {
        super(cache.vertexBuffer, cache.colorBuffer, cache.sizeBuffer);
        dataAcces = cache;
    }

    public void fetchData(){
        int depth = 2;
        int node = 6;
        int maxPage = 2;
        for (int page = 0; page <= maxPage ; page++) {
            final String query = buildQuery(Integer.toString(depth), Integer.toString(node),
                    Integer.toString(page));
            dataAcces.get(query);
        }
        centerOnCentroid();
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, vertexCount);
        this.dataAcces.rewindAllBuffer();
    }

    private String buildQuery(final String depth, final String node, final String page) {
        String SERVER_IP= "192.168.2.102:8080";

        Uri.Builder ub = new Uri.Builder();
        ub.scheme("http");
        ub.encodedAuthority(SERVER_IP);
        ub.appendQueryParameter("depth", depth);
        ub.appendQueryParameter("node", node);
        ub.appendQueryParameter("page", page);
        return ub.toString();
    }
}
