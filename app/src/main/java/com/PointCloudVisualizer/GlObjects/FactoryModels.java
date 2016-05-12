package com.PointCloudVisualizer.GlObjects;


public class FactoryModels {
    static float vertices[] = {
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f
    };
    static float colors[] = {
            0.0f,  1.0f,  0.0f,
            0.0f,  1.0f,  0.0f,
            1.0f,  0.5f,  0.0f,
            1.0f,  0.5f,  0.0f,
            1.0f,  0.0f,  0.0f,
            1.0f,  0.0f,  0.0f,
            0.0f,  0.0f,  1.0f,
            1.0f,  0.0f,  1.0f,
    };

    static float colorsBg[] = {
            0.0f,  0.0f,  0.0f,
            0.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            0.0f,  0.0f,  0.0f,
            0.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
    };

    static byte indices[] = {
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            3, 7, 4, 3, 4, 0,
            4, 7, 6, 4, 6, 5,
            3, 0, 1, 3, 1, 2
    };

    public static PointBufferModelGL buildCenterPoint(){
        float[] vertex = {0f,0f,0f};
        float[] color = {1f,0f,0f};
        return new PointBufferModelGL(vertex, color);
    }

    public static PolyBufferModelGL buildPlane(){
        float[] vertex = {
                -1f, -0.0f, 0f,
                1f, 1.0f, 0f,
                -1f, 1.0f, -0f,
                -1f, -0.0f, 0f,
                1f, -0.0f,  0f,
                1f, 1.0f,  0f};

        float[] color = {
                0f, 0f, 0f,
                255 * 0.3f, 0.0f, 0.0f,
                255 * 0.3f, 0.0f, 0.0f,
                0f, 0f, 0f,
                0f, 0f, 0f,
                255 * 0.3f,0.0f, 0.0f};

        return new PolyBufferModelGL(vertex, color);
    }

    public static PolyIndexBufferModelGL buildCube(){
        return new PolyIndexBufferModelGL(vertices, colors, indices);
    }

    public static PolyIndexBufferModelGL buildBackground(){
        return new PolyIndexBufferModelGL(vertices, colorsBg, indices);
    }
}
