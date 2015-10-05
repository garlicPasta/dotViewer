package com.example.jakob.PointCloudVisualizer.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;


public class PlyParser {


    private enum State{
        Header, Vertices, Indices
    }

    private int propertyCounter;
    private LinkedList<float[]> vertexes;
    private float[] vertexBuffer;
    private FloatBuffer vertexBufferDirect;
    private FloatBuffer colorBufferDirect;
    private float[] colorBuffer;
    private LinkedList<float[]> indices;
    private State parserState = State.Header;
    private int verticesCounter;
    private int currentVerticesCounter;
    private int indexVertices;
    private int indexColor;


    public PlyParser(Context context, int resourceId) {
        currentVerticesCounter = 0;
        propertyCounter = 0;
        vertexes = new LinkedList<>();
        indices = new LinkedList<>();

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

    private void parseLine(String nextLine) {

        switch (parserState){
            case Header:
                processHeaderLine(nextLine);
                break;
            case Vertices:
                propertyLine(nextLine);
                break;
            case Indices:
                processIndicesLine(nextLine);
                break;
            default:
        }
    }

    private void processHeaderLine(String nextLine){
            if (nextLine.contains("end_header")){
                parserState = State.Vertices;

                ByteBuffer byteBuf = ByteBuffer.allocateDirect(3 * 4 * verticesCounter);
                byteBuf.order(ByteOrder.nativeOrder());
                vertexBufferDirect = byteBuf.asFloatBuffer();
                vertexBufferDirect.position(0);

                byteBuf = ByteBuffer.allocateDirect(3 * 4 * verticesCounter);
                byteBuf.order(ByteOrder.nativeOrder());
                colorBufferDirect = byteBuf.asFloatBuffer();
                colorBufferDirect.position(0);

                indexVertices= 0;
                indexColor = 0;
            }else {
                if (nextLine.contains("element vertex")) {
                    verticesCounter = Integer.valueOf(nextLine.split(" ")[2]);
                }
                else if (nextLine.contains("property")){
                    propertyCounter++;
                }
            }
        }

    private void propertyLine(String nextLine) {
        if (currentVerticesCounter == verticesCounter){
            parserState = State.Indices;
            return;
        }

        String[] list = nextLine.split(" ");

        for(int i=0; i < 3 ; i++) {
            vertexBufferDirect.put(Float.valueOf(list[i]));
        }
        for(int i=3; i < propertyCounter ; i++) {
            colorBufferDirect.put(Float.valueOf(list[i]) / 255.0f);
        }
        currentVerticesCounter++;
    }

    private void processIndicesLine(String nextLine) {
        String[] list = nextLine.split(" ");
        float[] listCoords = new float[3];

        for (int i=0; i < 3 ; i++) {
            listCoords[i] = Float.parseFloat(list[i]);
        }
        indices.add(listCoords);
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBufferDirect;
    }

    public FloatBuffer getColorBuffer() {
        return colorBufferDirect;
    }
}


