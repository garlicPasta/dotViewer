package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.jakob.PointCloudVisualizer.GlObjects.MultiResolutionTreeGLOwner;
import com.example.jakob.PointCloudVisualizer.GlObjects.MultiResolutionTreeGL;
import com.google.protobuf.InvalidProtocolBufferException;


public class MRTRequest {

    static public void sendRequest(RequestQueue rQ, MultiResolutionTreeGLOwner owner){
        ProtoRequest request = new ProtoRequest(Request.Method.GET,
                QueryFactory.buildMRTQuery().toString(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "ProtoRequest failed");
                    }
                }, owner);
        rQ.add(request);
    }

    static class ProtoRequest extends Request<MultiResTreeProtos.MRTree> {
        MultiResolutionTreeGLOwner owner;

        public ProtoRequest(int method, String url, Response.ErrorListener listener,
                            MultiResolutionTreeGLOwner owner) {
            super(method, url, listener);
            this.owner = owner;
        }

        @Override
        protected Response<MultiResTreeProtos.MRTree> parseNetworkResponse(NetworkResponse response) {
            MultiResTreeProtos.MRTree b = null;
            try {
                b = MultiResTreeProtos.MRTree.parseFrom(response.data);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return Response.success(b, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(MultiResTreeProtos.MRTree tree) {
            MultiResolutionTreeGL remote = new MultiResolutionTreeGL(tree);
            owner.setMultiResolutionTree(remote);
            //scene.addModels(octree.exportChildren());
        }
    }
}
