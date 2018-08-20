package org.lwjglb.engine.graph.Gyro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class GyroListener implements Runnable {

    int port = 1234;

    Socket socket;

    BufferedReader reader;

    Gyroscope g;

    boolean running = true;

    public GyroListener(Gyroscope g) {

        this.g=g;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            socket = serverSocket.accept();

            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose(){

        try {
            reader.close();
            socket.close();
            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void run() {
        String temp="";

        String[] ta;

        while (running){

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                temp = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(temp == null){
                continue;
            }

            ta = temp.split(" ");

            g.setRx(Float.parseFloat(ta[1]));
            g.setRy(Float.parseFloat(ta[0]));
            g.setRz(-Float.parseFloat(ta[2]));

        }
    }
}
