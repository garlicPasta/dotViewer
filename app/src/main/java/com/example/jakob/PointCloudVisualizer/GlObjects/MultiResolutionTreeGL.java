package com.example.jakob.PointCloudVisualizer.GlObjects;


import com.example.jakob.PointCloudVisualizer.DataAccessLayer.MultiResTreeProtos;
import com.example.jakob.PointCloudVisualizer.util.BufferHelper;

import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildShortBuffer;

public class MultiResolutionTreeGL {

    public class OctreeNodeGL {
        public String id;
        boolean isLeaf;
        int pointCount;
        OctreeNodeGL[] octants = new OctreeNodeGL[8];
        float[] center;
        float[][] edgePoints;
        float length;
        BoxGL box;

        public OctreeNodeGL(MultiResTreeProtos.MRTree.MRNode node) {
            id = node.getId();
            isLeaf = node.getIsLeaf();
            pointCount = node.getPointCount();
            center = new float[]{(float) node.getCenter(0),
                    (float) node.getCenter(1),
                    (float) node.getCenter(2)};
            length = (float) node.getCellLength();
            box = new BoxGL(center, length);
            for (int i=0; i < node.getOctantCount(); i++){
                if (node.getOctant(i) != null)
                    octants[i] = new OctreeNodeGL(node.getOctant(i));
            }
            edgePoints = calculateEdgePoints();
        }

        private float[][] calculateEdgePoints() {
            float[] sign = {-1, 1};
            float[] vec = {center[0]/2, center[1]/2, center[2]/3};
            float [][] edges = new float[8][3];
            for (int i=0; i<2; i++){
                for (int j=0; j<2; j++){
                    for (int k=0; k<2; k++){
                        edges[4*i + 2*j + k] = new float[]{
                                center[0] + sign[i] * vec[0],
                                center[1] + sign[j] * vec[1],
                                center[2] + sign[k] * vec[2]
                            };
                    }
                }
            }
            return edges;
        }

        public void draw(){
            box.draw();
            for (int i = 0; i < octants.length; i++) {
                if (octants[i] != null)
                    octants[i].draw();
            }
        }

        public boolean isVisible(CameraGL c, ModelGl m){
            float[] MVmatrix = new float[16];
            multiplyMM(MVmatrix, 0, c.viewMatrix, 0, m.getModelMatrix(), 0);
            float[] projectedPoint = new float[4];
            for (float[] edgePoint : edgePoints){
                multiplyMV(projectedPoint, 0, MVmatrix, 0, edgePoint, 0);
                if (projectedPoint[0] > -1 && projectedPoint[0] < 1 &&
                        projectedPoint[1] > -1 && projectedPoint[1] < 1)
                    return true;
            }
            return false;
        }

        public float getDetailFactor(CameraGL c){
            return 0;
        }
    }


    public OctreeNodeGL root;

    public MultiResolutionTreeGL(MultiResTreeProtos.MRTree tree){
        root = new OctreeNodeGL(tree.getRoot());
    }

    public void draw(){
        root.draw();
    }

    public List<BoxGL> exportOctreeBoxes(){
        List<BoxGL> boxes = new LinkedList<>();
        _exportOctreeBoxes(root, boxes);
        return boxes;
    }

    private void _exportOctreeBoxes(OctreeNodeGL currentNode, List<BoxGL> boxes) {
        if (currentNode == null)
            return;
        boxes.add(currentNode.box);
        for (OctreeNodeGL node : currentNode.octants ) {
            _exportOctreeBoxes(node, boxes);
        }
    }

    /**
     * @return
     * List with all Ids of child notes
     */
    public List<String> getChildrenIds(){
        List<String> ids = new LinkedList<>();
        _getChildrenIds(root, ids);
        return ids;
    }

    private void _getChildrenIds(OctreeNodeGL currentNode, List<String> ids){
        if (currentNode.isLeaf){
            ids.add(currentNode.id);
            return;
        }
        for (OctreeNodeGL node : currentNode.octants ) {
            _getChildrenIds(node, ids);
        }
    }

    public List<String> getIdsMaxLevel(int level){
        List<String> ids = new LinkedList<>();
        _getIdsMaxLevel(root, ids, level);
        return ids;
    }

    private void _getIdsMaxLevel(OctreeNodeGL currentNode, List<String> ids, int level){
        if (currentNode == null)
            return;
        if (currentNode.isLeaf || level == 0){
            if (currentNode.pointCount > 0)
                ids.add(currentNode.id);
            return;
        }
        for (OctreeNodeGL node : currentNode.octants ) {
            _getIdsMaxLevel(node, ids, level-1);
        }
    }

    public List<String> getIdsConditional(){
        List<String> ids = new LinkedList<>();
        _getIdConditional(root, ids);
        return ids;
    }

    private void _getIdConditional(OctreeNodeGL currentNode, List<String> ids) {
        if (currentNode == null)
            return;
        if ((currentNode.isLeaf)){
            ids.add(currentNode.id);
            return;
        }


    }

    /**
     * @return
     * List with all Ids
     *
     */
    public List<String> getAllIds(){
        List<String> ids = new LinkedList<>();
        _getAllIds(root, ids);
        return ids;
    }

    private void _getAllIds(OctreeNodeGL currentNode, List<String> ids){
        ids.add(currentNode.id);
        if (currentNode.isLeaf){
            return;
        }
        for (OctreeNodeGL node : currentNode.octants ) {
            _getAllIds(node, ids);
        }
    }

    /**
     * @return
     * Function returns all Octans/Boxes which are children
     */
    public List<BoxGL> exportChildren(){
        List<BoxGL> children = new LinkedList<>();
        _exportChildren(root, children);
        return children;
    }

    private void _exportChildren(OctreeNodeGL currentNode, List<BoxGL> children){
        if (currentNode.isLeaf ){
            if (currentNode.pointCount > 0)
                children.add(currentNode.box);
            return;
        }
        for (OctreeNodeGL node : currentNode.octants ) {
            _exportChildren(node, children);
        }
    }
}
