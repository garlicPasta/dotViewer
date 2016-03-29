package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.jakob.PointCloudVisualizer.util.BufferHelper;
import com.google.protobuf.InvalidProtocolBufferException;

import java.nio.FloatBuffer;

public class SampleRequest {

    static public void sendRequest(String id, RequestQueue rQ, LRUDrawableCache cache){
        SampleProtoRequest request = new SampleProtoRequest(
                Request.Method.GET,
                QueryFactory.buildSampleQuery(id).toString(),
                id,
                cache,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "ProtoRequest failed");
                    }
                });
        rQ.add(request);
    }

    public static class SampleProtoRequest extends Request<RasterProtos.Raster>{
        private String key;
        private LRUDrawableCache cache;
        private int sampleCount;

        public SampleProtoRequest(int method, String url, String key, LRUDrawableCache cache,
                            Response.ErrorListener errorListener) {
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

            sampleCount = raster.getSampleCount();

            if (sampleCount == 0)
                return Response.success(raster, HttpHeaderParser.parseCacheHeaders(response));

            FloatBuffer vertices = BufferHelper.buildFloatBuffer(3 * sampleCount);
            FloatBuffer colors = BufferHelper.buildFloatBuffer(3 * sampleCount);
            FloatBuffer size = BufferHelper.buildFloatBuffer(sampleCount);

            for (RasterProtos.Raster.Point3DRGB p : raster.getSampleList()) {
                for (int i = 0; i < 3; i++) {
                    vertices.put(p.getPosition(i));
                    colors.put(p.getColor(i));
                }
                size.put(p.getSize());
            }
            vertices.rewind();
            colors.rewind();
            size.rewind();

            cache.set(key, vertices, colors, size);
            return Response.success(raster, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(RasterProtos.Raster response) {
            Log.d("Volley ", "Received " + sampleCount + "Points for key:" + key);
        }
    }
}

