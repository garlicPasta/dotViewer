package com.example.jakob.PointCloudVisualizer.GlObjects;


import android.util.Log;

import com.example.jakob.PointCloudVisualizer.DataAccessLayer.MultiResTreeProtos;
import com.example.jakob.PointCloudVisualizer.util.BufferHelper;
import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;

import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.opengl.Matrix.multiplyMV;

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
            float delta = length / 2;
            float [][] edges = new float[8][4];
            for (int i=0; i<2; i++){
                for (int j=0; j<2; j++){
                    for (int k=0; k<2; k++){
                        edges[4*i + 2*j + k] = new float[]{
                                center[0] + sign[i] * delta,
                                center[1] + sign[j] * delta,
                                center[2] + sign[k] * delta,
                                1
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

        public boolean isVisible(ModelGl m){
            float[] mvpMatrix = new float[16];
            MatrixHelper.multMatrices(
                    mvpMatrix,
                    m.camera.projectionMatrix,
                    m.camera.viewMatrix,
                    m.getModelMatrix()
            );

            float[][] projectedPoint = new float[8][4];
            for (int i = 0; i < edgePoints.length; i++) {
                float[] point = edgePoints[i];
                multiplyMV(projectedPoint[i], 0, mvpMatrix, 0, point, 0);
            }
            float[] boundingBox = getBoundingBox(projectedPoint);
            return rectangleIntersept(boundingBox);
        }

        private boolean rectangleIntersept(float[] box1){
            return  !(box1[0] + box1[2] < -.5 ||  .5 < box1[0] ||
                      box1[1] - box1[3] > .5 || -.5 > box1[1]);
        }


        /**
         * @param points
         * @return return top left point and width and height
         */
        private float[] getBoundingBox(float[][] points){
            float minX = points[0][0];
            float maxX = points[0][0];
            float minY = points[0][1];
            float maxY = points[0][1];
            for (float[] p: points){
                minX = Math.min(minX, p[0]);
                minY = Math.min(minY, p[1]);
                maxX = Math.max(maxX, p[0]);
                maxY = Math.max(maxY, p[1]);
            }
            return new float[]{minX, maxY, maxX - minX, maxY - minY};
        }

        public float getDetailFactor(CameraGL c){
            return 0;
        }
    }


    public OctreeNodeGL root;
    public Queue<OctreeNodeGL> activeNodes;
    ModelGl owner;

    public MultiResolutionTreeGL(MultiResTreeProtos.MRTree tree, ModelGl owner){
        root = new OctreeNodeGL(tree.getRoot());
        activeNodes = new ConcurrentLinkedQueue<>();
        this.owner = owner;
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
        activeNodes = new ConcurrentLinkedQueue<>();
        _getIdsMaxLevel(root, ids, level);
        return ids;
    }

    private void _getIdsMaxLevel(OctreeNodeGL currentNode, List<String> ids, int level){
        if (currentNode == null)
            return;
        if (currentNode.isLeaf || level == 0){
            if (currentNode.pointCount > 0) {
                ids.add(currentNode.id);
                activeNodes.add(currentNode);
            }
            return;
        }
        for (OctreeNodeGL node : currentNode.octants ) {
            if (node.isVisible(owner)) {
                _getIdsMaxLevel(node, ids, level - 1);
            }
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

    public List<BoxGL> exportActiceNodes(){
        List<BoxGL> boxes = new LinkedList<>();
        for (OctreeNodeGL node : activeNodes)
            boxes.add(node.box);
        return boxes;
    }
}
