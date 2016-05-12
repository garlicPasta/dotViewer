package com.PointCloudVisualizer.util;


import android.content.Context;

public class NvmParser extends Parser {

    int lineCounter;
    int cameraCounter;
    int verticesCounter;
    


    public NvmParser(Context context, int resourceId){
        super(context, resourceId);
        lineCounter = 0;
    }

    @Override
    protected void parseLine(String line) {
        if (line.isEmpty()) {
            return;
        }
        if (parserState == State.Header) {
            if (lineCounter == 0) {
                lineCounter++;
                return;
            }
            if (lineCounter == 1) {
                lineCounter++;
                cameraCounter = Integer.parseInt(line);
                return;
            } else if (cameraCounter > 0) {
                cameraCounter--;
                return;
            } else {
                parserState = State.Vertices;
                verticesCounter = Integer.parseInt(line);
                createAttributeBuffer(verticesCounter);
                return;
            }
        }
        if (parserState == State.Vertices){
            if (verticesCounter > 0){
                verticesCounter--;
                String[] list = line.split(" ");
                for(int i=0; i < 3 ; i++) {
                    vertexBufferDirect.put(Float.valueOf(list[i]));
                }
                for(int i=3; i < 6; i++) {
                    colorBufferDirect.put(Float.valueOf(list[i]) / 255.0f);
                }
            }
            return;
        }
    }
}
