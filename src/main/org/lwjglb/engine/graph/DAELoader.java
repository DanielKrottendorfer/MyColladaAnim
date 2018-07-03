package org.lwjglb.engine.graph;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.graph.Animation.AnimationData;
import org.lwjglb.engine.graph.Animation.Joint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DAELoader{

    static ArrayList<String> lines = new ArrayList<>();

    public static Mesh loadAnimatedMesh(String path) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String line = br.readLine();
            while (line != null){
                lines.add(line.trim());
                line = br.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        XMLNode geometries = buildXMLStructure("library_geometries");
        XMLNode animations = buildXMLStructure("library_animations");
        XMLNode controllers = buildXMLStructure("library_controllers");
        XMLNode visual_scenes = buildXMLStructure("library_visual_scenes");

        /*
        GEOMETRIE DATA
         */


        //Creating a temporary String Array to store Values
        String[] temp = geometries.findNode("float_array", "positions-array","").getValues().split(" ");
        //Loading the values into the temporary String Array and the pars them into theyr respective arrays
        Vector4f[] vertecis = Vector4fArrayParser(temp);


        temp = geometries.findNode("float_array", "normals-array","").getValues().split(" ");
        Vector4f[] normals = Vector4fArrayParser(temp);


        temp = geometries.findNode("float_array", "map-0-array","").getValues().split(" ");
        Vector2f[] uvmap = Vector2fArrayParser(temp);


        temp = geometries.findNode("p>", "","").getValues().split(" ");

        int[] faces = intArrayParser(temp);


        /*
        CONTROLLER DATA
         */


        temp = controllers.findNode("v>","","").getValues().split(" ");

        int[] v =intArrayParser(temp);

        temp = controllers.findNode("vcount","","").getValues().split(" ");

        int[] vcount = intArrayParser(temp);


        AnimationData[] ad = new AnimationData[vertecis.length];

        for( int i = 0,j = 0 ; i < ad.length ; i++){
            int[] matI = new int[vcount[i]];
            int[] wieI = new int[vcount[i]];
            for(int y = 0 ; y < vcount[i] ; y++,j+=2){
                matI[y] = v[j];
                wieI[y] = v[j+1];
            }
            ad[i] = new AnimationData(matI,wieI);
        }

        Joint[] JointTransM = buildJointTransforms(animations,controllers,visual_scenes);



        return null;
    }

    private static Joint[] buildJointTransforms(XMLNode animations, XMLNode controllers, XMLNode visual_scenes) {

        XMLNode rootNode = visual_scenes.findNode("node","Armature","Armature");
        JointConstruct rootJoint;



        return null;

    }

    private class JointConstruct{
        JointConstruct[] children;
        String name;
    }

    private static Vector2f[] Vector2fArrayParser(String[] temp) {
        int len = temp.length;
        if(len%2>0)
            return null;

        Vector2f[] vr = new Vector2f[len/2];

        for(int i = 0;i<len;i+=2){
            vr[i]= new Vector2f(Float.parseFloat(temp[i]),Float.parseFloat(temp[i+1]));
        }

        return vr;
    }

    private static Vector4f[] Vector4fArrayParser(String[] temp) {
        int len = temp.length;
        if(len%3>0)
            return null;

        Vector4f[] vr = new Vector4f[len/3];

        for(int i = 0;i<len;i+=3){
            vr[i]= new Vector4f(Float.parseFloat(temp[i]),Float.parseFloat(temp[i+1]),Float.parseFloat(temp[i+2]),1.0f);
        }

        return vr;
    }

    private static float[] floatArrayParser(String[] temp) {
        float[] fr = new float[temp.length];
        for (int i = 0;i<temp.length;i++)
            fr[i] = Float.parseFloat(temp[i]);

        return fr;
    }

    private static int[] intArrayParser(String[] temp) {
        int[] fr = new int[temp.length];
        for (int i = 0;i<temp.length;i++)
            fr[i] = Integer.parseInt(temp[i]);

        return fr;
    }

    private static Matrix4f[] MatrixLoader(String[] temp) {

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
    private static Mesh reorder(List<Vector3f> posList, List<AnimationData> ad, List<Vector3f> normList, List<Vector2f> textCoordList,
                                   List<Integer> facesList, Matrix4f[] bindPoseM, Matrix4f[] jointTransMat, float[] weights){
        /*
        System.out.println(posList);
        System.out.println(normList);
        System.out.println(textCoordList);
        System.out.println(facesList);
        */

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indeces=new ArrayList<>();

        List<Matrix4f[]> JTM = new ArrayList<>();
        List<Matrix4f[]> BPM = new ArrayList<>();
        List<float[]> W = new ArrayList<>();

        for(int i=0;i<facesList.size();i+=3){
            indeces.add(i);
            indeces.add(i+1);
            indeces.add(i+2);
            vertices.add(posList.get(facesList.get(i)));
            normals.add(normList.get(facesList.get(i+1)));
            textures.add(textCoordList.get(facesList.get(i+2)));

        }

        float[] vert = new float[vertices.size()*3];
        float[] tex = new float[textures.size()*2];
        float[] norm = new float[normals.size()*3];
        int[] indec = new int[indeces.size()];
        int i=0;

        for(Vector3f f:vertices){
            vert[i]=f.x;
            vert[i+1]=f.z;
            vert[i+2]=-f.y;
            i+=3;
        }
        i=0;
        for(Vector2f f:textures){
            tex[i]=f.x;
            tex[i+1]=1-f.y;
            i+=2;
        }
        i=0;
        for(Vector3f f:normals){
            norm[i]=f.x;
            norm[i+1]=f.z;
            norm[i+2]=f.y;
            i+=3;
        }
        i=0;
        for(int y:indeces){
            indec[i]=y;
            i++;
        }



        return new Mesh(vert,tex,norm,indec);
    }
    private static XMLNode buildXMLStructure(String sn) {
        int start;

        XMLNode xn;

        for (start = 0 ; start < lines.size() ; start++){
            if(lines.get(start).contains(sn))
                break;
        }

        xn = new XMLNode();
        xn.setSign(sn);

        XMLNode handle = xn;

        start++;

        while (true){

            String line = lines.get(start);

            if(line.startsWith("</")){

                if(line.startsWith("</"+sn))
                    return xn;

                handle = handle.getParent();

            } else{

                XMLNode child = new XMLNode();

                if(!line.contains(" ")){
                    child.setSign(line.substring(1,line.indexOf(">")));
                    handle.addChild(child);
                    child.setParent(handle);
                    handle = child;
                    start++;
                    continue;
                }

                child.setSign(line.substring(1,line.indexOf(" ")));


                if( line.contains("id=")) {
                    String helper = line.substring(line.indexOf("id=")+4);
                    child.setId(helper.substring(0,helper.indexOf("\"")));
                }

                if( line.contains("name=")) {
                    String helper = line.substring(line.indexOf("name=")+6);
                    child.setName(helper.substring(0,helper.indexOf("\"")));
                }

                if( line.contains("count=")) {
                    String helper = line.substring(line.indexOf("count=")+7);
                    child.setCount(Integer.parseInt(helper.substring(0,helper.indexOf("\"") )));
                }

                if( line.indexOf(">")+1<line.length()) {
                    String helper = line.substring(line.indexOf(">") + 1);
                    child.setValues(helper.substring(0,helper.indexOf ("<")));
                }

                child.setParent(handle);
                handle.addChild(child);
                if(!line.contains("/>")){
                    handle = child;
                }
            }
            start++;
        }
    }
}
