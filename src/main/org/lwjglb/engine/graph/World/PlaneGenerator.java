package org.lwjglb.engine.graph.World;

import org.joml.*;
import org.lwjgl.system.CallbackI;
import org.lwjglb.engine.OpenSimplexNoise;
import org.lwjglb.engine.graph.Mesh;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static org.joml.SimplexNoise.noise;

public class PlaneGenerator {

    private static double MAX_DOUBEL = Double.MAX_VALUE/2;


    public static Mesh generateFlatPlane(float X,float Z,float length, float width, int vCL, int vCW, int featuresize) {

        Vector3f[] positions = new Vector3f[vCL*vCW];
        Vector3f[] colors = new Vector3f[vCL*vCW];

        OpenSimplexNoise sn = new OpenSimplexNoise(0);

        int columns = vCW-1;

        int rows = vCL-1;

        float wSeparation = width/columns;
        float lSeparation = length/rows;

        System.out.println(wSeparation+" "+lSeparation);



        /*

            GENERATE POSITIONS

         */



        for(int i=0;i<vCL;i++){

            //System.out.print("\n i "+i+" ");

            for(int y=0;y<vCW;y++){

                float ns = (float) sn.eval(((float)i)/featuresize,((float)y)/featuresize,0);

                ns++;
                ns/=2;

                colors[(i*vCW)+y] = new Vector3f(ns,ns,ns);

                ns*=5;

                positions[(i*vCW)+y] = new Vector3f(X+wSeparation*i,0,Z+lSeparation*y);

                //System.out.print("y "+y+" "+positions[(y*vCL)+i]+" , ");

            }

        }
        /*

            GENERATE INDICES

         */

        class Face{

            private int[] indices = new int[3];

            private Face(int a, int b, int c){
                indices[0] = a;
                indices[1] = b;
                indices[2] = c;
            }

            public String toString(){

                return indices[0]+", "+indices[1]+", "+indices[2];

            }
        }



        ArrayList<Face> triangles = new ArrayList<>();

        for(int i = 0 ; i<rows ; i++){

            for(int y = 0 ; y<columns ; y++){

                int node = y+(i*vCW);

                triangles.add(new Face(node,node+1,node+vCW));

                //System.out.print(triangles.get(triangles.size()-1)+" , ");

                triangles.add(new Face(node+1,node+1+vCW,node+vCW));

                //System.out.print(triangles.get(triangles.size()-1)+" , ");
            }

            //System.out.println();

        }

        //System.out.println(triangles.size());


        int[] faceI = new int[triangles.size()*3];

        for(int i = 0,y=0;i<faceI.length;i+=3,y++){

            int[] face = triangles.get(y).indices;

            faceI[i]=face[0];
            faceI[i+1]=face[1];
            faceI[i+2]=face[2];

        }

        /*

            GENERATE NORMALS

        */

        Vector3f[] normals = new Vector3f[rows*columns*2];

        for(int i = 0,y=0; i<faceI.length;i+=3,y++){


            Vector3f a = new Vector3f(positions[faceI[i]]);
            Vector3f b = new Vector3f(positions[faceI[i+1]]);
            Vector3f c = new Vector3f(positions[faceI[i+2]]);

            b.sub(a);
            c.sub(a);

            b.cross(c);

            b.normalize();

            normals[y] = b;

        }

        return reorder(positions,normals,colors,faceI);

    }


    private static Vector3f hsvColorLerp(Color color1, Color color2, float v) {

        float[] HSBval1 = Color.RGBtoHSB(color1.getRed(),color1.getGreen(),color1.getBlue(),null);
        float[] HSBval2 = Color.RGBtoHSB(color2.getRed(),color2.getGreen(),color2.getBlue(),null);

        float hueDiff = HSBval2[0]-HSBval1[0] ;
        float satDiff = HSBval2[1]-HSBval1[1] ;
        float lumDiff = HSBval2[2]-HSBval1[2] ;

        hueDiff*=v;
        satDiff*=v;
        lumDiff*=v;

        HSBval1[0]+=hueDiff;
        HSBval1[1]+=satDiff;
        HSBval1[2]+=lumDiff;

        Color c =new Color(Color.HSBtoRGB(HSBval1[0],HSBval1[1],HSBval1[2]));

        return new Vector3f(((float)c.getRed())/255f,((float)c.getGreen())/255f,((float)c.getBlue())/255f);

    }

    public static Mesh reorder(Vector3f[] positions, Vector3f[] normals, Vector3f[] colors, int[] faceI) {

        ArrayList<Float> pL = new ArrayList<>();
        ArrayList<Float> nL = new ArrayList<>();
        ArrayList<Vector3f> cL = new ArrayList<>();


        ArrayList<Integer> iL = new ArrayList<>();


        for(int i = 0,y = 0;i<faceI.length;i+=3,y++){

            pL.add(positions[faceI[i]].x);
            pL.add(positions[faceI[i]].y);
            pL.add(positions[faceI[i]].z);

            pL.add(positions[faceI[i+1]].x);
            pL.add(positions[faceI[i+1]].y);
            pL.add(positions[faceI[i+1]].z);

            pL.add(positions[faceI[i+2]].x);
            pL.add(positions[faceI[i+2]].y);
            pL.add(positions[faceI[i+2]].z);

            cL.add(colors[faceI[i]]);
            cL.add(colors[faceI[i+1]]);
            cL.add(colors[faceI[i+2]]);


            nL.add(normals[y].x);
            nL.add(normals[y].y);
            nL.add(normals[y].z);

            nL.add(normals[y].x);
            nL.add(normals[y].y);
            nL.add(normals[y].z);

            nL.add(normals[y].x);
            nL.add(normals[y].y);
            nL.add(normals[y].z);


            iL.add(i);
            iL.add(i+1);
            iL.add(i+2);

        }

        float[] pos = new float[pL.size()];

        for(int i = 0; i< pos.length;i++){

            pos[i] = pL.get(i);

        }

        Vector3f[] col = new Vector3f[cL.size()];

        for(int i = 0; i< col.length;i++){

            col[i] = cL.get(i);

        }

        float[] norm = new float[nL.size()];

        for(int i = 0; i< norm.length;i++){

            norm[i] = nL.get(i);

        }

        int[] indices = new int[iL.size()];


        for(int i = 0; i< indices.length;i++){

            indices[i] = iL.get(i);

        }

        return new Mesh(pos,col,norm,indices);

    }

    public static Mesh generateNoisePlane(float X,float Z,float length, float width, int vCL, int vCW,OpenSimplexNoise noise, double featuresize) {

        Vector3f[] positions = new Vector3f[vCL*vCW];
        Vector3f[] colors = new Vector3f[vCL*vCW];


        int columns = vCW-1;

        int rows = vCL-1;

        float wSeparation = width/columns;
        float lSeparation = length/rows;




        /*

            GENERATE POSITIONS

         */



        for(int i=0;i<vCL;i++){

            //System.out.print("\n i "+i+" ");

            for(int y=0;y<vCW;y++){

                float ns = (float) noise.eval(((X+wSeparation*i))/(featuresize),((Z+lSeparation*y))/(featuresize),0);

                ns++;
                ns/=2;

                colors[(i*vCW)+y] = hsvColorLerp(Color.GREEN,Color.RED,ns);

                ns*=1.3;
                ns*=ns;
                ns*=ns;
                ns*=ns;
                ns*=ns;


                positions[(i*vCW)+y] = new Vector3f(X+wSeparation*i,ns,Z+lSeparation*y);

                //System.out.print("y "+y+" "+positions[(i*vCW)+y]+" , ");

            }

        }
        //System.out.println();


        //System.out.println("a: "+positions[0]+" b: "+positions[vCW]+" c: "+positions[(vCW*vCL)-1]+" c: "+positions[(vCW*vCL)-1-vCW]);
        /*

            GENERATE INDICES

         */

        class Face{

            private int[] indices = new int[3];

            private Face(int a, int b, int c){
                indices[0] = a;
                indices[1] = b;
                indices[2] = c;
            }

            public String toString(){

                return indices[0]+", "+indices[1]+", "+indices[2];

            }
        }



        ArrayList<Face> triangles = new ArrayList<>();

        for(int i = 0 ; i<rows ; i++){

            for(int y = 0 ; y<columns ; y++){

                int node = y+(i*vCW);

                triangles.add(new Face(node,node+1,node+vCW));

                //System.out.print(triangles.get(triangles.size()-1)+" , ");

                triangles.add(new Face(node+1,node+1+vCW,node+vCW));

                //System.out.print(triangles.get(triangles.size()-1)+" , ");
            }

            //System.out.println();

        }

        //System.out.println(triangles.size());


        int[] faceI = new int[triangles.size()*3];

        for(int i = 0,y=0;i<faceI.length;i+=3,y++){

            int[] face = triangles.get(y).indices;

            faceI[i]=face[0];
            faceI[i+1]=face[1];
            faceI[i+2]=face[2];

        }

        /*

            GENERATE NORMALS

        */

        Vector3f[] normals = new Vector3f[rows*columns*2];

        for(int i = 0,y=0; i<faceI.length;i+=3,y++){


            Vector3f a = new Vector3f(positions[faceI[i]]);
            Vector3f b = new Vector3f(positions[faceI[i+1]]);
            Vector3f c = new Vector3f(positions[faceI[i+2]]);

            b.sub(a);
            c.sub(a);

            b.cross(c);

            b.normalize();

            normals[y] = b;

        }

        return reorder(positions,normals,colors,faceI);

    }

}
