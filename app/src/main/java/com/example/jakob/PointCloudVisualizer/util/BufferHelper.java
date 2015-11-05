package com.example.jakob.PointCloudVisualizer.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BufferHelper {

    public static FloatBuffer buildFloatBuffer(float[] array, int typeSize){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(array.length * typeSize);
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = byteBuf.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    public static ByteBuffer buildByteBuffer(byte[] array){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length);
        byteBuffer.put(array);
        byteBuffer.position(0);
        return byteBuffer;
    }
}

