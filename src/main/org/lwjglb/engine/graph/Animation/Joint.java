package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;

import java.util.Arrays;

public class Joint {

    public Matrix4f jointBindPositionTransformM;
    public Matrix4f jointKeyFPositionsTransformM[];

    public float[] timestamps;

    private int index;

    private String name;

    private Joint[] children;

    public Joint(Matrix4f jointBindPositionTransformM, String name) {
        this.jointBindPositionTransformM = jointBindPositionTransformM;
        this.name = name;
    }
    public Joint findJoint(String name){

        if(this.name.contains(name)){
            return this;
        }

        if(hasChildren())
            for(Joint child:children){
                Joint temp = child.findJoint(name);
                if(temp!= null)
                    return temp;
        }

        return null;

    }
    public Joint findJoint(int index){

        if(this.index==index){
            return this;
        }

        if(hasChildren())
            for(Joint child:children){
                Joint temp = child.findJoint(index);
                if(temp!= null)
                    return temp;
            }

        return null;

    }

    @Override
    public String toString() {
        return "Joint{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Joint[] getChildren() {
        return children;
    }

    public void setChildren(Joint[] children) {
        this.children = children;
    }

    public boolean hasChildren(){
        return children != null;
    }

    public void printTree(String indent){
        if(children!=null){
            for (Joint child:children)
                child.printTree(indent + "|");
        }
    }
}
