package org.lwjglb.engine.graph;

import java.util.ArrayList;

public class PlaneGenerator {
    public static Mesh generate(float length, float width, int vCL, int vCW) {

        // vCL Vertecis Count Length
        float xIncr = width / vCW;
        // vCW Vertecis Count Width
        float zIncr = length / vCL;

        ArrayList<Float> positions = new ArrayList<Float>();

        for (int i = 0, x = 0; i < vCL; i++) {
            for (int y = 0; y < vCW; y++, x++) {
                positions.add(y * xIncr);
                positions.add(1.0f);
                positions.add(i * zIncr);
            }
        }

        float[] pos = new float[positions.size()];
        for(int i =0 ; i< pos.length;i++)
            pos[i]=positions.get(i);

        int sCX = vCW-1;
        int sCZ = vCL-1;

        // Square count length and width


        ArrayList<face> ih = new ArrayList<face>();

        for(int i = 0,x=0;i<vCL-1;i++){
            for (int y = 0;y<vCW-1;y++,x++){
                ih.add(new face(i*vCW+y,i*vCW+y+1,i*vCW+y+vCW));
                ih.add(new face(i*vCW+y+1,i*vCW+y+vCW+1,i*vCW+y+vCW));
            }
        }

        int[] indec = new int[ih.size()*3];

        for(int i = 0,y=0;i<ih.size();i++,y+=3){
            indec[y]=ih.get(i).getIndecis()[0];
            indec[y+1]=ih.get(i).getIndecis()[1];
            indec[y+2]=ih.get(i).getIndecis()[2];
        }

        float[] f ={0};
        float[] t ={0};
        return new Mesh(pos,t,f,indec);
    }
}
