package org.lwjglb.engine.graph;

import java.util.Arrays;

public class face {
    int[] indecis;

    @Override
    public String toString() {
        return "face{" +
                "indecis=" + Arrays.toString(indecis) +
                '}';
    }

    public int[] getIndecis() {
        return indecis;
    }

    public void setIndecis(int[] indecis) {
        this.indecis = indecis;
    }

    public face(int a, int b, int c) {
        indecis = new int[3];
        this.indecis[0]=a;
        this.indecis[1]=b;
        this.indecis[2]=c;
    }

}
