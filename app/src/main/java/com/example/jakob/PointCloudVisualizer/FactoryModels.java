package com.example.jakob.PointCloudVisualizer;


public class FactoryModels {

    public static PointModelGL buildCenterPoint(){
        float[] vertex = {0f,0f,0f};
        float[] color = {1f,0f,0f};
        return new PointModelGL(vertex, color);
    }

    public static PolyModelGL buildPlane(){
        float[] vertex = {0f,0f,0f,
                1f,0f,1f,
                0f, 0f, 1f,
                0f,0f,0f,
                1f, 0f, 0f,
                1f, 0f, 1f};

        float[] color = {1f,1f,1f,
                0f, 0f, 0f,
                0f, 0f, 0f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                0f, 0f, 0f};
        return new PolyModelGL(vertex, color);
    }
}
