package com.example.jakob.PointCloudVisualizer.GlObjects;



import com.example.jakob.PointCloudVisualizer.DataAccessLayer.MultiResTreeProtos;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildShortBuffer;

public class OctreeWireGL{

    private class OctreeNodeGL {
        OctreeWireGL[] octants = new OctreeWireGL[8];
        float[] center;
        float length;
        BoxGL box;

        public OctreeNodeGL(MultiResTreeProtos.MRTree.MRNode node) {
            super();
            center = new float[]{(float) node.getCenter(0),
                    (float) node.getCenter(1),
                    (float) node.getCenter(2)};
            length = (float) node.getCellLength();
            box = new BoxGL(center, length);
        }

        public void draw(){
            box.draw();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i] != null)
                    octants[i].draw();
            }
        }
    }

    private class BoxGL extends ModelGL{

        protected ShortBuffer mIndexBuffer;

        float[] cubeVertices = new float[]{
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
        };

        short indices[] = { 0, 2, 1,
                0, 3, 2,
                1,2,6,
                6,5,1,
                4,5,6,
                6,7,4,
                2,3,6,
                6,3,7,
                0,7,3,
                0,4,7,
                0,1,5,
                0,5,4
        };


        BoxGL(float[] center,float length){
            super(ByteBuffer.allocateDirect(24 * 4).asFloatBuffer());
            for (int i = 0; i < cubeVertices.length / 3 ; i++) {
                for (int j = 0; j < 3; j++) {
                    cubeVertices[3 * i + j] *= length;
                    cubeVertices[3 * i + j] += center[j];
                }
            }
            this.mVertexBuffer.put(cubeVertices);
            this.mIndexBuffer = buildShortBuffer(indices);
        }

        @Override
        public void draw(){
            glDrawElements(GL_LINE_STRIP, 12, GL_UNSIGNED_SHORT, mIndexBuffer);
        }
    }

    private OctreeNodeGL root;

    OctreeWireGL(MultiResTreeProtos.MRTree tree){
        root = new OctreeNodeGL(tree.getRoot());
    }

    public void draw(){
        root.draw();
    }
}
