package org.lwjglb.engine.graph.Gyro;

public class Gyroscope {

    private float rx;
    private float  ry;
    private float rz;

    public Gyroscope() {
        this.rx = 0;
        this.ry = 0;
        this.rz = 0;
    }

    public synchronized float getRx() {
        return rx;
    }

    public synchronized void setRx(float rx) {
        this.rx = rx;
    }

    public synchronized float getRy() {
        return ry;
    }

    public synchronized void setRy(float ry) {
        this.ry = ry;
    }

    public synchronized float getRz() {
        return rz;
    }

    public synchronized void setRz(float rz) {
        this.rz = rz;
    }
}
