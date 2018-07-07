package org.lwjglb.engine.graph;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import org.lwjglb.engine.graph.Animation.AnimatedModel;
import org.lwjglb.engine.graph.Animation.Joint;
import org.lwjglb.engine.graph.Animation.SkinPoint;
import org.lwjglb.engine.graph.Mesh;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ColladaLoader {

    private static final int maxJoint = 3;

    public static AnimatedModel loadAnimatedModel(String path) {


        Document document;
        document = getDOMParsedDocument(path);


        Element rootNode = document.getRootElement();

        Element geometries = findElement(rootNode,"library_geometries");
        Element animation = findElement(rootNode,"library_animations");
        Element controllers = findElement(rootNode,"library_controllers");
        Element visual_scenes = findElement(rootNode,"library_visual_scenes");

        /*
                    LOAD GEOMETRIE DATA
         */

        String temp ;
        temp = findChildByID(geometries,"Cube-mesh-positions-array").getValue();
        Vector4f[] vertecis = Vector4fArrayParser(temp.split(" "));


        temp = findChildByID(geometries,"Cube-mesh-normals-array").getValue();
        Vector4f[] normals = Vector4fArrayParser(temp.split(" "));


        temp = findChildByID(geometries,"Cube-mesh-map-0-array").getValue();
        Vector2f[] uvMap = Vector2fArrayParser(temp.split(" "));


        temp= findChildByName(geometries,"p").getValue();
        int[] faces = IntArrayParser(temp.split(" "));


        /*
                    LOAD SKELETAL DATA
         */

        Joint rootJoint = buildHirachy(findChildByID(visual_scenes,"Armature"));

        ArrayList<String> jointIndex = new ArrayList<>(Arrays.asList(findChildByID(controllers,"Armature_Cube-skin-joints-array").getValue().split(" ")));

        addIndecis(rootJoint,jointIndex);

        addJointTransforms(rootJoint,animation);

        multiplyMatrices(rootJoint);

        Joint[] joints = generateJointArray(rootJoint);

        temp = findChildByID(controllers,"Armature_Cube-skin-bind_poses-array").getValue();

        Matrix4f[] bindPos = Matrix4fArrayParser(temp.split(" "));

        for(int i = 0;i<bindPos.length;i++){
            joints[i].jointBindPositionTransformM=bindPos[i];
        }


        /*
                LOAD  CONTROLLER DATA
         */

        temp = findChildByID(controllers,"Armature_Cube-skin-weights-array").getValue();
        float[] weights = floatArrayParser(temp.split(" "));

        temp = findChildByName(controllers,"vcount").getValue();
        int[] vC =IntArrayParser(temp.split(" "));

        temp = findChildByName(controllers,"v").getValue();
        int[] v = IntArrayParser(temp.split(" "));

        SkinPoint[] points= generateSkinPoints(vertecis,weights,vC,v);

        int faceAttributes = findChildByName(geometries,"triangles").getChildren().size()-1;



        return reorderDynamicM(points,normals,uvMap,faces,faceAttributes,joints);
    }

    private static void multiplyMatrices(Joint rootJoint) {
        if(rootJoint.hasChildren()) {
            for (Joint child : rootJoint.getChildren()) {
                for (int i = 0; i < child.jointKeyFPositionsTransformM.length; i++) {
                    child.jointKeyFPositionsTransformM[i].mul(rootJoint.jointKeyFPositionsTransformM[i]);
                }
                multiplyMatrices(child);
            }
        }
    }

    private static Joint[] generateJointArray(Joint rootJoint) {
        ArrayList<Joint> jl = new ArrayList<>();

        for(int i = 0;true;i++){
            Joint j = rootJoint.findJoint(i);
            if(j==null) {
                break;
            }

            jl.add(j);
        }

        Joint[] jr = new Joint[jl.size()];

        for(int i = 0;i<jr.length;i++){
            jr[i] = jl.get(i);
        }

        return jr;
    }

    public static Mesh loadStaticMesh(String path) {


        Document document;
        document = getDOMParsedDocument(path);


        Element rootNode = document.getRootElement();


        Element geometries = findElement(rootNode,"library_geometries");
        /*
                    LOAD GEOMETRIE DATA
         */

        String temp ;
        temp = findChildByID(geometries,"Cube-mesh-positions-array").getValue();
        Vector4f[] vertecis = Vector4fArrayParser(temp.split(" "));


        temp = findChildByID(geometries,"Cube-mesh-normals-array").getValue();
        Vector4f[] normals = Vector4fArrayParser(temp.split(" "));


        temp = findChildByID(geometries,"Cube-mesh-map-0-array").getValue();
        Vector2f[] uvMap = Vector2fArrayParser(temp.split(" "));


        temp= findChildByName(geometries,"p").getValue();
        int[] faces = IntArrayParser(temp.split(" "));

        int faceAttributes = findChildByName(geometries,"triangles").getChildren().size()-1;

        return reorderStaticM(vertecis,normals,uvMap,faces,faceAttributes);
    }

    private static Mesh reorderStaticM(Vector4f[] vertecis, Vector4f[] normals, Vector2f[] uvMap, int[] faces, int faceAttributes) {

        ArrayList<Float> v = new ArrayList<>();
        ArrayList<Float> n = new ArrayList<>();
        ArrayList<Float> t = new ArrayList<>();


        int[] indices = new int[faces.length];




        for(int i = 0,j=0;i<faces.length;j+=3,i+=faceAttributes){

            v.add(vertecis[faces[i]].x);
            v.add(vertecis[faces[i]].z);
            v.add(vertecis[faces[i]].y);

            n.add(normals[faces[i+1]].x);
            n.add(normals[faces[i+1]].z);
            n.add(normals[faces[i+1]].y);

            t.add(uvMap[faces[i+2]].x);
            t.add(1-uvMap[faces[i+2]].y);

            indices[j]=j;
            indices[j+1]=j+1;
            indices[j+2]=j+2;
        }

        float[] positions = new float[v.size()];
        float[] normV = new float[n.size()];
        float[] texture = new float[t.size()];


        for(int i = 0;i<positions.length;i++){
            positions[i] = v.get(i);
        }
        for(int i = 0;i<normV.length;i++){
            normV[i] = n.get(i);
        }
        for(int i = 0;i<texture.length;i++){
            texture[i] = t.get(i);
        }

        return new Mesh(positions,texture,normV,indices);

    }

    private static AnimatedModel reorderDynamicM(SkinPoint[] skinPoints, Vector4f[] normals, Vector2f[] uvMap, int[] faces, int faceAttributes, Joint[] joints) {

        ArrayList<Float> v = new ArrayList<>();
        ArrayList<Float> w = new ArrayList<>();
        ArrayList<Integer> m = new ArrayList<>();
        ArrayList<Float> n = new ArrayList<>();
        ArrayList<Float> t = new ArrayList<>();
        ArrayList<Integer> jc = new ArrayList<>();


        int[] indices = new int[faces.length];


        for(int i = 0;i<faces.length;i+=faceAttributes){


            SkinPoint sp=skinPoints[faces[i]];

            Vector4f pos = sp.getPositon();

            v.add(pos.x);
            v.add(pos.z);
            v.add(pos.y);

            int jointCount = sp.getJointI().length;

            jc.add(jointCount);

            for(int y = 0;y<jointCount;y++){
                w.add(sp.getWeights()[y]);
                m.add(sp.getJointI()[y]);
                //System.out.println("weight: "+sp.getWeights()[y]);
                //System.out.println("JT;:    "+sp.getJointI()[y]+"\n");
            }

            for(int y = jointCount ; y<maxJoint ; y++ ){
                w.add(0.0f);
                m.add(-1);
            }


            n.add(normals[faces[i+1]].x);
            n.add(normals[faces[i+1]].z);
            n.add(normals[faces[i+1]].y);

            t.add(uvMap[faces[i+2]].x);
            t.add(1-uvMap[faces[i+2]].y);

            indices[i]=i;
            indices[i+1]=i+1;
            indices[i+2]=i+2;
        }

        float[] positions = new float[v.size()];
        float[] normV = new float[n.size()];
        float[] texture = new float[t.size()];

        float[] weights = new float[w.size()];
        int[] matrixIndices = new int[m.size()];
        int[] jointCount = new int[jc.size()];


        for(int i = 0;i<positions.length;i++){
            positions[i] = v.get(i);
        }
        for(int i = 0;i<normV.length;i++){
            normV[i] = n.get(i);
        }
        for(int i = 0;i<texture.length;i++){
            texture[i] = t.get(i);
        }
        for(int i = 0;i<weights.length;i++){
            weights[i] = w.get(i);
        }
        for(int i = 0;i<matrixIndices.length;i++){
            matrixIndices[i] = m.get(i);
        }
        for(int i = 0;i<jointCount.length;i++){
            jointCount[i] = jc.get(i);
        }

        return new AnimatedModel(positions,normV,texture,weights,indices,matrixIndices,joints);

    }



    private static SkinPoint[] generateSkinPoints(Vector4f[] vertecis, float[] weights, int[] vC, int[] v) {

        int len = vertecis.length;

        SkinPoint[] skin = new SkinPoint[len];

        int vProg=0;

        for (int i = 0;i< len;i++){

            int j = vC[i];
            float[] w = new float[j];
            int[] mI = new int[j];

            for(int y = 0;y<j;y++,vProg+=2){
                w[y]=weights[v[vProg+1]];
                mI[y]=v[vProg];
            }


            skin[i]=new SkinPoint(vertecis[i],w,mI);


        }

        return skin;
    }

    private static void addJointTransforms(Joint rootJoint, Element animation) {

        List<Element> jointNodes = animation.getChildren();

        for (Element jointN:jointNodes){
            String name = jointN.getAttributeValue("id");
            name = name.substring(name.indexOf("_")+1,name.length());
            name = name.substring(0,name.indexOf("_pose"));

            Joint tempj = rootJoint.findJoint(name);

            if(tempj==null)
                continue;

            float[] timeStamps = floatArrayParser(findChildByID(jointN,"Armature_"+name+"_pose_matrix-input-array").getValue().split(" "));

            tempj.jointKeyFPositionsTransformM = Matrix4fArrayParser(findChildByID(jointN,"Armature_"+name+"_pose_matrix-output-array").getValue().split(" "));
            tempj.timestamps = timeStamps;

        }

    }

    private static void addIndecis(Joint rootJoint, ArrayList<String> jointIndex) {

        for(int i = 0;i<jointIndex.size();i++){
            if(rootJoint.getName().contains(jointIndex.get(i)))
                rootJoint.setIndex(i);

        }
        if(rootJoint.hasChildren())
            for(Joint j:rootJoint.getChildren()){
                addIndecis(j,jointIndex);
            }
    }

    private static Joint buildHirachy(Element armature) {

        String name = "";
        Matrix4f JointBindPosTransformM = null;
        Joint[] children;

        Element rootNode = null;

        for(Element jointNode:armature.getChildren()){
            if(jointNode.getName().equals("node")){
                String helper = jointNode.getAttribute("id").getValue();
                name = helper.substring(helper.indexOf("_")+1,helper.length());
                JointBindPosTransformM = Objects.requireNonNull(Matrix4fArrayParser(jointNode.getChildren().get(0).getValue().split(" ")))[0];
                rootNode = jointNode;
            }
        }

        Joint rootJoint = new Joint(JointBindPosTransformM,name);

        buldHirachyR(rootJoint,rootNode);

        return rootJoint;

    }

    private static void buldHirachyR(Joint rootJoint, Element rootNode){
        ArrayList<Joint> childL = new ArrayList<>();
        ArrayList<Element> childNodes = new ArrayList<>();

        for(Element jointNode:rootNode.getChildren()){
            if(jointNode.getName().equals("node")){
                String helper = jointNode.getAttribute("id").getValue();
                String name = helper.substring(helper.indexOf("_")+1,helper.length());
                Matrix4f JointBindPosTransformM = Objects.requireNonNull(Matrix4fArrayParser(jointNode.getChildren().get(0).getValue().split(" ")))[0];
                childL.add(new Joint(JointBindPosTransformM,name));
                childNodes.add(jointNode);
            }
        }

        int childCount = childL.size();

        if(childCount>0) {
            Joint[] children = new Joint[childL.size()];

            for (int i = 0; i < children.length; i++) {
                children[i] = childL.get(i);
            }

            rootJoint.setChildren(children);

            for(int i = 0; i < children.length;i++) {
                buldHirachyR(children[i],childNodes.get(i));
            }
        }
    }

    private static Matrix4f[] Matrix4fArrayParser(String[] temp) {

        int len=temp.length;

        if(len%16>0)
            return null;

        float[] fl = new float[len];

        for(int i = 0; i<len;i++)
            fl[i]=Float.parseFloat(temp[i]);

        Matrix4f[] ma = new Matrix4f[len/16];

        for (int i = 0;i<len/16;i++){
            ma[i]=new Matrix4f();
            Vector4f row = new Vector4f();
            for( int y = 0;y<4;y++){
                row.x = fl[(i*16)+(y*4)];
                row.y = fl[(i*16)+(y*4)+1];
                row.z = fl[(i*16)+(y*4)+2];
                row.w = fl[(i*16)+(y*4)+3];
                ma[i].setRow(y,row);
            }
        }
        return ma;
    }
    private static Vector4f[] Vector4fArrayParser(String[] temp) {


        int len = temp.length;
        if(len%3>0)
            return null;

        Vector4f[] vr = new Vector4f[len/3];

        for(int i = 0,y=0;i<len;i+=3,y++){
            vr[y]= new Vector4f(Float.parseFloat(temp[i]),Float.parseFloat(temp[i+1]),Float.parseFloat(temp[i+2]),0.0f);
        }

        return vr;
    }

    private static Vector2f[] Vector2fArrayParser(String[] temp) {
        int len = temp.length;
        if(len%2>0)
            return null;

        Vector2f[] vr = new Vector2f[len/2];

        for(int i = 0,y=0;i<len;i+=2,y++){
            vr[y]= new Vector2f(Float.parseFloat(temp[i]),Float.parseFloat(temp[i+1]));
        }

        return vr;
    }

    private static float[] floatArrayParser(String[] temp) {
        float[] fr = new float[temp.length];
        for (int i = 0;i<temp.length;i++)
            fr[i] = Float.parseFloat(temp[i]);

        return fr;
    }


    private static int[] IntArrayParser(String[] temp) {
        int[] fr = new int[temp.length];
        for (int i = 0;i<temp.length;i++)
            fr[i] = Integer.parseInt(temp[i]);

        return fr;
    }

    private static Element findChildByName(Element rootElement, String name) {

        if (rootElement.getName().equals(name)){
            return rootElement;
        }


        for(Element child:rootElement.getChildren() ){
            Element temp = findChildByName(child,name);
            if(temp!= null)
                return temp;
        }
        return null;
    }

    private static Element findChildByID(Element rootElement, String id) {
        if (rootElement.getAttributeValue("id") != null){
            if (rootElement.getAttributeValue("id").startsWith(id)) {
                return rootElement;
            }
        }

        for(Element child:rootElement.getChildren() ){
            Element temp = findChildByID(child,id);
            if(temp!= null)
                return temp;
        }
        return null;
    }

    private static Element findElement(Element rootElement, String name) {
        for(Element e:rootElement.getChildren()){
            if(e.getName().contains(name))
                return e;
        }
        return null;
    }

    private static void printTree(Element rootNode) {
        printTree(rootNode,"");
    }

    private static void printTree(Element rootNode, String indent) {

        for(Element e:rootNode.getChildren()) {
            String print;
            print = "";
            if(e.getAttributeValue("id")!=null) {
                print+=e.getAttributeValue("id");
            }


            //System.out.println(indent+e.getName()+" "+print);
            printTree(e,indent+" ");
        }

    }
    private static void printTreeWithName(Element rootNode,String name, String indent) {

        for(Element e:rootNode.getChildren()) {
            String print;
            print = "";
            if(e.getAttributeValue("id")!=null) {
                print+=e.getAttributeValue("id");
            }

            //if(e.getName().startsWith(name))
                //System.out.println(indent+e.getName()+" "+print);
            printTreeWithName(e,name,indent+" ");
        }

    }

    private static Document getDOMParsedDocument(final String fileName) {
        Document document = null;
        try {
            DocumentBuilderFactory factory;
            factory = DocumentBuilderFactory.newInstance();
            //If want to make namespace aware.
            //factory.setNamespaceAware(true);
            DocumentBuilder documentBuilder;
            documentBuilder = factory.newDocumentBuilder();
            org.w3c.dom.Document w3cDocument;
            w3cDocument = documentBuilder.parse(fileName);
            document = new DOMBuilder().build(w3cDocument);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return document;
    }
}
