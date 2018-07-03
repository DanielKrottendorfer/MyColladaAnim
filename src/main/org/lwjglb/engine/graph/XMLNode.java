package org.lwjglb.engine.graph;

import java.util.ArrayList;

public class XMLNode {
    static ArrayList<String> lines = new ArrayList<>();

    private String sign = "";
    private String id = "";
    private String values = "";
    private String name = "";
    private int count = 0;
    private XMLNode parent;
    private ArrayList<XMLNode> children = new ArrayList<>();

    public XMLNode findNode(String sign,String id,String name){

        if((this.sign.contains(sign)&&this.id.contains(id))&&this.name.contains(name)){
            return this;
        }

        for(XMLNode child:children){
            XMLNode temp = child.findNode(sign,id,name);
            if(temp!= null)
                return temp;
        }

        return null;

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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public XMLNode getParent() {
        return parent;
    }

    public void setParent(XMLNode parent) {
        this.parent = parent;
    }

    public void addChild(XMLNode child){
        children.add(child);
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public ArrayList<XMLNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<XMLNode> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "XMLNode{" +
                "sign='" + sign + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count + '\'' +
                ", values='" + values + '\'' +
                '}';
    }
}
