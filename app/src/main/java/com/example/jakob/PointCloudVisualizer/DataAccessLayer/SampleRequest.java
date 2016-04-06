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

    static public void sendRequest(final String id, RequestQueue rQ, final LRUDrawableCache cache){
        SampleProtoRequest request = new SampleProtoRequest(
                Request.Method.GET,
                QueryFactory.buildSampleQuery(id).toString(),
                id,
                cache,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "ProtoRequest failed");
                        cache.remove(cache.getNode(id));
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
            if (!cache.activeNodes.contains(key))
                return null;
            DrawableBufferNode node = cache.getNode(key);

            RasterProtos.Raster raster = null;
            try {
                raster = RasterProtos.Raster.parseFrom(response.data);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            sampleCount = raster.getSampleCount();
            if (sampleCount == 0) {
                cache.remove(node);
                return Response.success(raster, HttpHeaderParser.parseCacheHeaders(response));
            }
            cache.updatePointCount(sampleCount);
            node.setPointCount(sampleCount);

            int bufferSize = 7 * sampleCount;

            FloatBuffer xyzrgbsBuffer = BufferHelper.buildFloatBuffer(bufferSize);

            for (RasterProtos.Raster.Point3DRGB p : raster.getSampleList()) {
                for (int i = 0; i < 3; i++)
                    xyzrgbsBuffer.put(p.getPosition(i));
                for (int i = 0; i < 3; i++)
                    xyzrgbsBuffer.put(p.getColor(i));
                xyzrgbsBuffer.put(p.getSize());
            }

            xyzrgbsBuffer.rewind();
            node.setXyzrgbsBuffer(xyzrgbsBuffer);

            return Response.success(raster, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(RasterProtos.Raster response) {
            Log.d("Volley ", "Received " + sampleCount + " Points for key:" + key);
        }

        @Override
        public boolean equals(Object obj){
            SampleProtoRequest request = (SampleProtoRequest) obj;
            return key.equals(request.key);
        }

        @Override
        public void cancel(){
            super.cancel();
            cache.remove(cache.getNode(key));
        }

        @Override
        public int hashCode(){
            return key.hashCode();
        }
    }
}

