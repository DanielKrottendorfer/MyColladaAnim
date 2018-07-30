package org.lwjglb.engine.graph.Wall;

import org.joml.Vector3f;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.World.PlaneGenerator;
public class WallGenerator {

    public static Mesh generateSquareWall( float xPos,float yPos,float zPos,float length, float width, float height, float thickness){

        Vector3f[] positions = new Vector3f[16];

        positions[0]= new Vector3f(xPos,yPos,zPos);
        positions[1]= new Vector3f(xPos+width,yPos,zPos);
        positions[2]= new Vector3f(xPos+width,yPos,zPos+length);
        positions[3]= new Vector3f(xPos,yPos,zPos+length);

        positions[4]= new Vector3f(xPos+thickness,yPos,zPos+thickness);
        positions[5]= new Vector3f(xPos+width-thickness,yPos,zPos+thickness);
        positions[6]= new Vector3f(xPos+width-thickness,yPos,zPos+length-thickness);
        positions[7]= new Vector3f(xPos+thickness,yPos,zPos+length-thickness);


        positions[8]= new Vector3f(xPos,yPos+height,zPos);
        positions[9]= new Vector3f(xPos+width,yPos+height,zPos);
        positions[10]= new Vector3f(xPos+width,yPos+height,zPos+length);
        positions[11]= new Vector3f(xPos,yPos+height,zPos+length);

        positions[12]= new Vector3f(xPos+thickness,yPos+height,zPos+thickness);
        positions[13]= new Vector3f(xPos+width-thickness,yPos+height,zPos+thickness);
        positions[14]= new Vector3f(xPos+width-thickness,yPos+height,zPos+length-thickness);
        positions[15]= new Vector3f(xPos+thickness,yPos+height,zPos+length-thickness);


        int[] faceI = {0,3,11,0,11,8,3,2,10,3,10,11,2,9,1,2,10,9,1,9,8,1,8,0,0,3,11,0,11,8,3,2,10,3,10,11,2,9,1,2,10,9,1,9,8,1,8,0,3,7,2,2,7,6,2,6,1,6,5,1,1,5,0,5,4,0,0,4,3,4,7,3,3,7,2,2,7,6,2,6,1,6,5,1,1,5,0,5,4,0,0,4,3,4,7,3};

        for(int i = 16*3;i<faceI.length;i++){

            faceI[i]+=8;

        }

        for(int i = 0 ; i<24;i++){
            faceI[i]+=4;
        }

        Vector3f[] normals = new Vector3f[72];

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

        Vector3f[] colors = new Vector3f[72];

        for (int i = 0;i<colors.length;i++){
            colors[i] = new Vector3f(0.5f,0.5f,0.5f);
        }
        return PlaneGenerator.reorder(positions,normals,colors,faceI);

    }
}
