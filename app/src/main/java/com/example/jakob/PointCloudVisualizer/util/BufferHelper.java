package com.example.jakob.PointCloudVisualizer.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferHelper {

    public static FloatBuffer buildFloatBuffer(float[] array, int typeSize){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(array.length * typeSize);
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = byteBuf.asFloatBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }

    public static ShortBuffer buildShortBuffer(short[] array) {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(array.length * 2);
        byteBuf.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = byteBuf.asShortBuffer();
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

