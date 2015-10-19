package com.example.jakob.PointCloudVisualizer.util;

import android.test.AndroidTestCase;
import com.example.jakob.PointCloudVisualizer.R;

import java.nio.FloatBuffer;

import static org.junit.Assert.*;

public class PlyParserTest extends AndroidTestCase {

    PlyParser parser;

    private static int EXAMPLE_POINTS = 4;

    public void setUp() {
        parser = new PlyParser(getContext(), R.raw.test_model);
        assertNotNull(parser);
    }

    public void testColorBuffer() {
        float d = 255;
        float[] expectValue = {
             168, 158, 141,
            132, 126, 110,
            70, 70, 58,
            138, 134, 112
        };
        FloatBuffer actualValue;
        for (int i=0; i<expectValue.length; ++i){
            expectValue[i] /= d;
        }
        actualValue = parser.getColorBuffer();
        assertEquals(EXAMPLE_POINTS * 3, parser.getVertexBuffer().capacity());
        assertArrayEquals(expectValue, actualValue.array(), 0.0f);
    }

    public void testVertexBuffer(){
        float[] expectValue = {
                9.56013171023f, -1.02948639236f, 4.04212536447f,
                9.07586942007f, 1.55056111621f, 3.63505982427f,
                6.52307595559f, 7.44617049958f, 2.85959904481f,
                9.11698100566f, 1.58804032241f, 3.31590484207f
        };
        FloatBuffer actualValue = parser.getVertexBuffer();
        assertEquals(EXAMPLE_POINTS * 3, parser.getVertexBuffer().capacity());
        assertArrayEquals(expectValue, actualValue.array(), 0.0f);
    }
}
