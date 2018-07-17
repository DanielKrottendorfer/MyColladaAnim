package org.lwjglb.engine.graph.Animation;

import org.joml.Matrix4f;

import java.util.Arrays;

public class Joint {

    public Matrix4f jointBindPositionTransformM;
    public Matrix4f jointKeyFPositionsTransformM[];

    private int index;

    private String name;

    private Joint[] children;

    private int jointC;


    public Joint(Joint joint){
        this.jointBindPositionTransformM= new Matrix4f(joint.jointBindPositionTransformM);
        this.jointKeyFPositionsTransformM = new Matrix4f[joint.jointKeyFPositionsTransformM.length];

        for(int i = 0 ; i<joint.jointKeyFPositionsTransformM.length;i++){
            this.jointKeyFPositionsTransformM[i] = new Matrix4f(joint.jointKeyFPositionsTransformM[i]);
        }

        this.index = joint.index;

        this.name = joint.name;

        this.jointC = joint.jointC;

        if(joint.getChildren()!=null) {
            this.children = new Joint[joint.getChildren().length];
            for (int i = 0; i < joint.getChildren().length; i++) {
                this.children[i] = new Joint(joint.getChildren()[i]);
            }
        }else {
            this.children = null;
        }
    }

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
        String childnames="";

        if(children!=null) {
            for (int i = 0; i < children.length; i++) {

                childnames += children[i].getName() + ", ";

            }
        }

        return "Joint{" +
                "index=" + index +
                ", name='" + name +
                " children= "+childnames + '\'' +
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

    public void printTree(){
        String matrices = "";

        for(int i=0;i<jointKeyFPositionsTransformM.length;i++){
            matrices += "i: "+i+"\n"+jointKeyFPositionsTransformM[i];
        }

        matrices +="bp: +\n"+jointBindPositionTransformM;


        System.out.println("name: "+name+" index: "+index+"\n"+matrices);


        if(children!=null){
            for(Joint child : children){
                System.out.println(" name: "+child.getName()+" index: "+child.getIndex()+"\n");
            }
            for (Joint child:children)
                child.printTree();
        }
    }

    public int getJointC() {
        return jointC;
    }

    public void setJointC(int jointC) {
        this.jointC = jointC;
    }
}
