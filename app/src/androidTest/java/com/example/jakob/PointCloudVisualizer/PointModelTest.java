package com.example.jakob.PointCloudVisualizer;

import com.example.jakob.PointCloudVisualizer.GlObjects.PointModelGL;

import junit.framework.TestCase;

import java.util.Arrays;

public class PointModelTest extends TestCase {

    PointModelGL cube;

    private float vertices[] = {
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f
    };

    public void setUp() {
        cube = new PointModelGL(vertices);
        assertNotNull(cube);
    }

    public void testSize(){
        assertEquals(cube.vertexCount, 8);
    }

    public void testCentroid(){
        assertTrue(Arrays.equals(cube.getCentroid(), new float[]{0f, 0f, 0f}));
    }
}