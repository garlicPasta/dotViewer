package com.example.jakob.PointCloudVisualizer;

import junit.framework.TestCase;

public class PointModelTest extends TestCase {

    PointModel cube;

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
        cube = new PointModel(vertices);
        assertNotNull(cube);
    }

    public void testSize(){
        assertEquals(cube.getSize(),8);
    }
}