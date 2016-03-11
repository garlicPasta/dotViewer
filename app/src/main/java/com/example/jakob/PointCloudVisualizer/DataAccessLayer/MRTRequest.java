package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.protobuf.InvalidProtocolBufferException;


public class MRTRequest {
    private RequestQueue rQ;


    public MRTRequest(RequestQueue rQ) {
        this.rQ = rQ;
    }

    public void sendRequest(){
        ProtoRequest request = new ProtoRequest(Request.Method.GET,
                QueryFactory.buildMRTQuery().toString(),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "ProtoRequest failed");
                    }
                });
        rQ.add(request);
    }

    class ProtoRequest extends Request<MultiResTreeProtos.MRTree> {

        public ProtoRequest(int method, String url, Response.ErrorListener listener) {
            super(method, url, listener);
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

        }
    }
}
