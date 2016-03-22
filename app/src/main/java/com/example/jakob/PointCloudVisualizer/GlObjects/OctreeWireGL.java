package com.example.jakob.PointCloudVisualizer.GlObjects;


import com.example.jakob.PointCloudVisualizer.DataAccessLayer.MultiResTreeProtos;
import com.example.jakob.PointCloudVisualizer.util.BufferHelper;

import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildShortBuffer;

public class OctreeWireGL{

    private class OctreeNodeGL {
        OctreeNodeGL[] octants = new OctreeNodeGL[8];
        float[] center;
        float length;
        BoxGL box;

        public OctreeNodeGL(MultiResTreeProtos.MRTree.MRNode node) {
            center = new float[]{(float) node.getCenter(0),
                    (float) node.getCenter(1),
                    (float) node.getCenter(2)};
            length = (float) node.getCellLength();
            box = new BoxGL(center, length);
            for (int i=0; i < node.getOctantCount(); i++){
                if (node.getOctant(i) != null)
                    octants[i] = new OctreeNodeGL(node.getOctant(i));
            }
        }

        public void draw(){
            box.draw();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i] != null)
                    octants[i].draw();
            }
        }
    }

    public static class BoxGL extends ModelGL{

        protected ShortBuffer mIndexBuffer;

         float[] cubeVertices = new float[]{
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
        };

        float[] cubeColor = new float[]{
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
                128f, 192f, 64f,
        };

        short indices[] = {
                0,1,
                1,2,
                2,3,
                3,0,
                4,5,
                5,6,
                6,7,
                7,4,
                0,4,
                1,5,
                2,6,
                3,7,
        };


        public BoxGL(float[] center, float length){
            super(BufferHelper.buildFloatBuffer(24));
            for (int i = 0; i < cubeVertices.length / 3 ; i++) {
                for (int j = 0; j < 3; j++) {
                    cubeVertices[3 * i + j] *= length;
                    cubeVertices[3 * i + j] += center[j];
                }
            }
            this.mVertexBuffer.put(cubeVertices);
            this.mVertexBuffer.position(0);
            this.mColorBuffer.put(cubeColor);
            this.mColorBuffer.position(0);
            this.mIndexBuffer = buildShortBuffer(indices);
        }

        @Override
        public void draw(){
            glDrawElements(GL_LINES, indices.length, GL_UNSIGNED_SHORT, mIndexBuffer);
        }
    }

    private OctreeNodeGL root;

    public OctreeWireGL(MultiResTreeProtos.MRTree tree){
        root = new OctreeNodeGL(tree.getRoot());
    }

    public void draw(){
        root.draw();
    }

    public List<BoxGL> exportOctreeBoxes(){
        List<BoxGL> boxes = new LinkedList<>();
        _addToBoxes(root, boxes);
        return boxes;
    }

    private void _addToBoxes(OctreeNodeGL currentNode, List<BoxGL> boxes) {
        if (currentNode == null)
            return;
        boxes.add(currentNode.box);
        for (OctreeNodeGL node : currentNode.octants ) {
            _addToBoxes(node, boxes);
        }
    }
}
