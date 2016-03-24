package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.protobuf.InvalidProtocolBufferException;

public class PointRequest extends Request<RasterProtos.Raster> {
    private String key;
    private LRUCache cache;

    public PointRequest(int method, String url,LRUCache cache,
                        Response.ErrorListener errorListener, String key) {
        super(method, url, errorListener);
        this.key = key;
        this.cache = cache;
    }

    @Override
    protected Response<RasterProtos.Raster> parseNetworkResponse(NetworkResponse response) {
        RasterProtos.Raster raster = null;
        try {
            raster = RasterProtos.Raster.parseFrom(response.data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        float[] vertices = new float[3 * cache.POINT_COUNT];
        float[] colors = new float[3 * cache.POINT_COUNT];
        float[] size = new float[cache.POINT_COUNT];

        int j = 0;
        int k = 0;

        for (RasterProtos.Raster.Point3DRGB p : raster.getSampleList()) {
            for (int i = 0; i < 3; i++) {
                vertices[j] = p.getPosition(i);
                colors[j++] = p.getColor(i);
            }
            size[k++] = p.getSize();
        }
        cache.updateCache(key, vertices, colors, size);
        cache.vertexCount += k;
        //Log.d("Response", response.substring(0, 500));
        return Response.success(raster, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(RasterProtos.Raster response) {
        Log.d("Volley ", "Receive Request for " + key);
    }
}

