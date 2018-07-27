package org.lwjglb.engine.graph;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjglb.engine.Utils;

public class MyOBJLoader {

    public static Mesh loadMesh(String fileName) throws Exception {
        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;
                default:
                    // Ignore other lines
                    break;
            }
        }
        /*
        System.out.println("Vertecis");
        for(Vector3f f:vertices)
            System.out.print(f+" ,");
        System.out.println("Textrues");
        for(Vector2f f:textures)
            System.out.print(f+" ,");
        System.out.println("Normaks");
        for(Face f:faces)
            System.out.print(f.toString());
            */
        return reorderere(vertices, textures, normals, faces);
        //return reorderLists(vertices, textures, normals, faces);
    }

    private static Mesh reorderere(List<Vector3f> posList, List<Vector2f> textCoordList,
                                   List<Vector3f> normList, List<Face> facesList){

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        List<IdxGroup> groups= new ArrayList<>();
        List<Integer> indeces=new ArrayList<>();
        int i=0;
        for(Face f:facesList) {
            for(IdxGroup g:f.getFaceVertexIndices()){
                indeces.add(i);
                i++;
                vertices.add(posList.get(g.idxPos));
                textures.add(textCoordList.get(g.idxTextCoord));
                normals.add(normList.get(g.idxVecNormal));
            }
        }

        float[] vert = new float[vertices.size()*3];
        float[] tex = new float[textures.size()*2];
        float[] norm = new float[normals.size()*3];
        int[] indec = new int[indeces.size()];
         i=0;

        for(Vector3f f:vertices){
            vert[i]=f.x;
            vert[i+1]=f.y;
            vert[i+2]=f.z;
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
            norm[i]=f.z;
            norm[i+1]=f.x;
            norm[i+2]=f.y;
            i+=3;
        }
        i=0;
        for(int y:indeces){
            indec[i]=y;
            i++;
        }

        /*
        for(int y=0,j=0;y< vert.length;y+=3,j+=2,i++) {
            System.out.println("Index :"+indec[i]);
            System.out.println("Positions:");
            System.out.print(" x: "+vert[y]);
            System.out.print(" y: "+vert[y+1]);
            System.out.print(" z: "+vert[y+2]);
            System.out.println();
            System.out.println("Textures:");
            System.out.print(" x: "+tex[j]);
            System.out.print(" y: "+tex[j+1]);
            System.out.println();
            System.out.println("Normals:");
            System.out.print(" x: "+norm[y]);
            System.out.print(" y: "+norm[y+1]);
            System.out.print(" z: "+norm[y+2]);
            System.out.println();
            System.out.println();

        }
        */


        return new Mesh(vert,tex,norm,indec);
    }

    private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList,
                                     List<Vector3f> normList, List<Face> facesList) {

        List<Integer> indices = new ArrayList();
        // Create position array in the order it has been declared
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr;
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return new Mesh(posArr, textCoordArr, normArr, indicesArr);;
    }

    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Integer> indicesList,
                                          float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private IdxGroup[] idxGroups;

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public String toString(){
            return idxGroups[0].toString()+" "+idxGroups[1].toString()+" "+idxGroups[2].toString()+" ";
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }

        @Override
        public String toString() {
            return idxPos+"/"+idxTextCoord+"/"+idxVecNormal;
        }
    }
}
