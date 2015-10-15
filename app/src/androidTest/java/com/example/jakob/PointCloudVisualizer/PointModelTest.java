package com.example.jakob.PointCloudVisualizer;

import junit.framework.TestCase;

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
        assertEquals(cube.getSize(),8);
    }
}