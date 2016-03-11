package com.example.jakob.PointCloudVisualizer.DataAccessLayer;

import android.net.Uri;

public class QueryFactory {

    public static Uri buildMRTQuery(){
        Uri.Builder ub = new Uri.Builder();
        ub.scheme("http");
        ub.encodedAuthority(DataAcessLayer.SERVER_IP);
        ub.appendQueryParameter("mode", "tree");
        return ub.build();
    }

    public static Uri buildSampleQuery(String id){
        Uri.Builder ub = new Uri.Builder();
        ub.scheme("http");
        ub.encodedAuthority(DataAcessLayer.SERVER_IP);
        ub.appendQueryParameter("mode", "samples");
        ub.appendQueryParameter("id", id);
        return ub.build();
    }
}
