package com.example.jakob.PointCloudVisualizer.GlObjects;


import java.nio.FloatBuffer;

public class OctreeWireGL {

    private class BoxGL{
        float center;
        float length;
        FloatBuffer verticeBuffer;
        FloatBuffer indices;
        float[][] vertices;
        float[] cubeVertices = new float[]{
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
        };

        short indices[] = { 0, 2, 1,
                0, 3, 2,
                1,2,6,
                6,5,1,
                4,5,6,
                6,7,4,
                2,3,6,
                6,3,7,
                0,7,3,
                0,4,7,
                0,1,5,
                0,5,4
        };


        BoxGL(float[] center,float length){
            for (int i = 0; i < cubeVertices.length / 3 ; i++) {
                for (int j = 0; j < 3; j++) {
                    cubeVertices[3 * i + j] *= length;
                    cubeVertices[3 * i + j] += center[j];
                }
            }
        }

        private void draw(){
        }
    }

    private BoxGL root;

    OctreeWireGL(){

    }

    public void draw(){

    }
}
