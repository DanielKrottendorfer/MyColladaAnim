package org.lwjglb.engine.graph;

import org.joml.Planef;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.joml.SimplexNoise.noise;

public class PlaneGenerator {


    public static Mesh generate(float length, float width, int vCL, int vCW) {

        Vector3f[] positions = new Vector3f[vCL*vCW];
        Vector3f[] colors = new Vector3f[vCL*vCW];

        int columns = vCW-1;

        int rows = vCL-1;

        float wSeparation = width/columns;
        float lSeparation = length/rows;



        float[][] noise = generateSimplexNoise(vCW,vCL);


        /*

            GENERATE POSITIONS

         */



        for(int i=0;i<vCW;i++){

            for(int y=0;y<vCL;y++){

                positions[(i*vCL)+y] = new Vector3f(wSeparation*y,noise[i][y]*10,lSeparation*i);

                Vector3f f1 = new Vector3f(242f/255f,82f/255f,13f/255f);
                Vector3f f2 = new Vector3f(15f/255f,105f/255f,240f/255f);

                f1.mul(f2);

                f1.mul(noise[i][y]);

                colors[(i*vCL)+y] = new Vector3f(f1);

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

                triangles.add(new Face(y+(i*vCW),y+(i*vCW)+1,y+((i+1)*vCW)));

                //System.out.print(triangles.get(triangles.size()-1)+", ");


                triangles.add(new Face(y+(i*vCW)+1,y+((i+1)*vCW)+1,y+((i+1)*vCW)));

                //System.out.print(triangles.get(triangles.size()-1)+", ");

            }

            //System.out.println();

        }


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

    private static Mesh reorder(Vector3f[] positions, Vector3f[] normals, Vector3f[] colors, int[] faceI) {

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


    private static float[][] generateSimplexNoise(int width, int height){
        float[][] simplexnoise = new float[width][height];
        float frequency = 3.0f / (float) width;

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                simplexnoise[x][y] = noise(x * frequency,y * frequency);
                simplexnoise[x][y] = (simplexnoise[x][y] + 1) / 2;   //generate values between 0 and 1
            }
        }

        return simplexnoise;
    }
}
