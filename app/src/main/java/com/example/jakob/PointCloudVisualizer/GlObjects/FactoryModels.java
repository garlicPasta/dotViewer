package com.example.jakob.PointCloudVisualizer.GlObjects;


import com.example.jakob.PointCloudVisualizer.GlObjects.PointModelGL;
import com.example.jakob.PointCloudVisualizer.GlObjects.PolyModelGL;

public class FactoryModels {

    public static PointModelGL buildCenterPoint(){
        float[] vertex = {0f,0f,0f};
        float[] color = {1f,0f,0f};
        return new PointModelGL(vertex, color);
    }

    public static PolyModelGL buildPlane(){
        float[] vertex = {
                0f,0f,0f,
                1f,0f,1f,
                0f, 0f, 1f,
                0f,0f,0f,
                1f, 0f, 0f,
                1f, 0f, 1f};

        float[] color = {
                1f, 1f, 1f,
                0f, 0f, 0f,
                0f, 0f, 0f,
                1f, 1f, 1f,
                1f, 1f, 1f,
                0f, 0f, 0f};
        return new PolyModelGL(vertex, color);
    }

    public static PolyIndexModelGL buildCube(){

        float vertices[] = {
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f,  1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f
        };
        float colors[] = {
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                1.0f,  0.5f,  0.0f,
                1.0f,  0.5f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                0.0f,  0.0f,  1.0f,
                1.0f,  0.0f,  1.0f,
        };

        byte indices[] = {
                0, 4, 5, 0, 5, 1,
                1, 5, 6, 1, 6, 2,
                2, 6, 7, 2, 7, 3,
                3, 7, 4, 3, 4, 0,
                4, 7, 6, 4, 6, 5,
                3, 0, 1, 3, 1, 2
        };
        return new PolyIndexModelGL(vertices, colors, indices);
    }
}
