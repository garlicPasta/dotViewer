package com.PointCloudVisualizer.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class Parser {

    protected enum State{
        Header, Vertices
    }

    protected FloatBuffer vertexBufferDirect;
    protected FloatBuffer colorBufferDirect;
    protected  State parserState;


    public Parser(Context context, int resourceId) {
        parserState = State.Header;

        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                parseLine(nextLine);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
    }

    public Parser(String s){
        BufferedReader br = new BufferedReader(new StringReader(s));
        String line;
        try {
            while( (line=br.readLine()) != null )
            {
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void createAttributeBuffer(int verticesCounter){
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(3 * 4 * verticesCounter);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBufferDirect = byteBuf.asFloatBuffer();
        vertexBufferDirect.position(0);

        byteBuf = ByteBuffer.allocateDirect(3 * 4 * verticesCounter);
        byteBuf.order(ByteOrder.nativeOrder());
        colorBufferDirect = byteBuf.asFloatBuffer();
        colorBufferDirect.position(0);
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBufferDirect;
    }

    public FloatBuffer getColorBuffer() {
        return colorBufferDirect;
    }

    protected abstract void parseLine(String line);

}
