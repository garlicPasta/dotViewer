package com.example.jakob.PointCloudVisualizer.util;

import android.content.Context;

import java.nio.FloatBuffer;
import java.util.LinkedList;


public class PlyParser extends Parser {

    private enum State{
        Header, Vertices, Indices
    }

    private int propertyCounter;
    private LinkedList<float[]> indices;
    private State parserState = State.Header;
    private int verticesCounter;
    private int currentVerticesCounter;

    public PlyParser(Context context, int resourceId) {
        super(context, resourceId);
        currentVerticesCounter = 0;
        propertyCounter = 0;
    }

    protected void parseLine(String nextLine) {
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
                createAttributeBuffer(verticesCounter);
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
}
