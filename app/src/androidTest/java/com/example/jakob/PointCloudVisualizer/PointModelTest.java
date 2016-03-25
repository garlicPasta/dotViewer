package com.example.jakob.PointCloudVisualizer;

import com.example.jakob.PointCloudVisualizer.GlObjects.PointBufferModelGL;

import junit.framework.TestCase;

import java.util.Arrays;

public class PointModelTest extends TestCase {

    PointBufferModelGL cube;

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
        cube = new PointBufferModelGL(vertices);
        assertNotNull(cube);
    }

    public void testSize(){
        assertEquals(cube.vertexCount, 8);
    }

    public void testCentroid(){
        assertTrue(Arrays.equals(cube.getCentroid(), new float[]{0f, 0f, 0f}));
    }
}