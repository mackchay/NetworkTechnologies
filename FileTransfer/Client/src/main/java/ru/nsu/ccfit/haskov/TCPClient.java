package ru.nsu.ccfit.haskov;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class TCPClient {

    public void sendFile(TCPClientOptions options) {
        Socket socket;
        try {
            socket = new Socket(options.inetAddress(), options.port());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Client is connected to server on " + options.port());

        File file = new File(String.valueOf(options.path()));

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("ERROR: file " + options.path() +
                    " was not found.");
        }

        System.out.println("Ready for sending file...");

        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final byte[] bufferData = new byte[5000];
        int bytes = 0;
        int totalBytes = 0;
        long startTime, curTime;

        startTime = System.currentTimeMillis();
        try {
            while ((bytes = fileInputStream.read(bufferData)) != -1) {
                totalBytes += bytes;
                curTime = System.currentTimeMillis() - startTime;
                if (ServerUtils.convertToMBytesInSeconds(totalBytes, curTime) > options.speed()) {
                    Thread.sleep(1);
                }
                dataOutputStream.write(bufferData, 0, bytes);
            }
            fileInputStream.close();
            socket.shutdownOutput();

            boolean success = dataInputStream.readBoolean();
            if (success) {
                System.out.println("SUCCESS!");
            }
            else {
                System.out.println("FAIL!");
            }

            dataOutputStream.close();
            dataInputStream.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
