package org.lwjglb.engine.graph;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class MyXMLHandler extends DefaultHandler {
    //List to hold Employees object
    private List<Float> vertecis = new ArrayList<Float>();
    private List<Float> normals = new ArrayList<Float>();
    private List<Float> texture = new ArrayList<Float>();
    private List<Integer> indices = new ArrayList<Integer>();
    boolean vertexLine=false;
    boolean normalsLine=false;
    boolean textureLine=false;
    boolean triangles=false;

    boolean indicesLine=false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println(" qName: "+qName+" v1: "+attributes.getLocalName(0));
        if(qName.startsWith("float_array"))
            if(attributes.getLocalName(0)!=null)
                if(attributes.getLocalName(0).startsWith("id")) {
                    String id = attributes.getValue("id");
                    if(id.startsWith("Cone-mesh-positions-array"))
                        vertexLine=true;
                    else
                    if(id.startsWith("Cone-mesh-normals-array"))
                        normalsLine=true;
                    else
                    if(id.startsWith("Cone-mesh-map-0-array"))
                        textureLine=true;
                }
        if(qName.startsWith("triangles"))
            triangles=true;

        if(triangles && qName.startsWith("p"))
            indicesLine=true;
    }

    public void endElement (String uri, String localName, String qName) throws SAXException {
        vertexLine=false;
        normalsLine=false;
        textureLine=false;
        indicesLine=false;

        if(qName.startsWith("triangles")) {
            triangles = false;
        }

    }

    public void characters ( char ch[], int start, int length) throws SAXException {
        if (vertexLine == true) {
            String s="";
            for (int i = start;i<=start+length;i++){
                char c = ch[i];
                if (c == ' ' || i == start+length){
                    vertecis.add(Float.parseFloat(s));
                    s="";
                }
                s+=c;
            }
        }else
        if (normalsLine == true) {
            String s="";
            for (int i = start;i<=start+length;i++){
                char c = ch[i];
                if (c == ' ' || i == start+length){
                    normals.add(Float.parseFloat(s));
                    s="";
                }
                s+=c;
            }
        }else
        if (textureLine == true) {
            String s="";
            for (int i = start;i<=start+length;i++){
                char c = ch[i];
                if (c == ' ' || i == start+length){
                    texture.add(Float.parseFloat(s));
                    s="";
                }
                s+=c;
            }
        }else
        if (indicesLine == true) {
            String s="";
            for (int i = start;i<=start+length;i++){
                char c = ch[i];
                if (c == ' ' || i == start+length){
                    indices.add((int)Float.parseFloat(s));
                    s="";
                }
                s+=c;
            }
        }
    }

    public void endDocument(){

    }

    public List<Float> getVertecis() {
        return vertecis;
    }

    public List<Vector3f> getVertecisV3f() {
        List<Vector3f> v3f= new ArrayList<>();
        for(int i=0;i<vertecis.size();i+=3){
            v3f.add(new Vector3f(vertecis.get(i),vertecis.get(i+1),vertecis.get(i+2)));
        }
        return  v3f;
    }

    public List<Float> getNormals() {
        return normals;
    }
    public List<Vector3f> getNormalsV3f() {
        List<Vector3f> v3f= new ArrayList<>();
        for(int i=0;i<normals.size();i+=3){
            v3f.add(new Vector3f(normals.get(i),normals.get(i+1),normals.get(i+2)));
        }
        return  v3f;
    }

    public List<Float> getTexture() {
        return texture;
    }

    public List<Vector2f> getTextureV2f() {
        List<Vector2f> v2f= new ArrayList<>();
        for(int i=0;i<texture.size();i+=2){
            v2f.add(new Vector2f(texture.get(i),texture.get(i+1)));
        }
        return  v2f;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}