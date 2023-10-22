package ru.nsu.ccfit.haskov;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TCPServer {
    private static final String UPLOAD_DIR = "uploads";
    private static final int BUFFER_SIZE = 2000;

    private static final int MAX_FILE_LENGTH = 4096;
    private static final int MAX_CLIENTS = 10;

    private static final long PRINT_PERIOD = 3000;

    public void start(int port) {
        ExecutorService executorService = new ScheduledThreadPoolExecutor(MAX_CLIENTS);
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started at " + InetAddress.getLocalHost() + " and listening in " + port);
        } catch (IOException e) {
            throw new RuntimeException("ERROR: can't create server socket on port " + port);
        }

        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException("ERROR: can't accept socket.");
            }
            System.out.println("Client connected: " + clientSocket.getInetAddress()
                    + " Start receiving data.");
            executorService.execute(handleClient(clientSocket));
        }
    }

    public Runnable handleClient(Socket clientSocket) {
        return () -> {
            DataInputStream inputStream;
            DataOutputStream outputStream;
            String fileName;
            long fileSize;
            boolean success = false;
            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());

                fileName = inputStream.readUTF();
                fileSize = inputStream.readLong();

                if (fileName.length() > MAX_FILE_LENGTH) {
                    throw new RuntimeException("File name is incorrect.");
                }

                System.out.println("Preparing for uploading " + fileName);
                long startTime, curTime, checkpointTime;
                FileOutputStream fileOutputStream = getFileOutputStream(fileName);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesReceived;
                long totalBytesReceived = 0, iterBytesReceived = 0;

                startTime = System.currentTimeMillis();
                checkpointTime = startTime;
                while ((bytesReceived = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesReceived);
                    totalBytesReceived += bytesReceived;
                    iterBytesReceived += bytesReceived;
                    curTime = System.currentTimeMillis() - checkpointTime;
                    if (curTime > PRINT_PERIOD) {
                        System.out.println("Current speed: " +
                                ServerUtils.convertToMBytesInSeconds(iterBytesReceived , curTime) +
                                " MBytes/s");
                        System.out.println("Average speed: " +
                                ServerUtils.convertToMBytesInSeconds
                                (totalBytesReceived, System.currentTimeMillis() - startTime)
                                + " MBytes/s\n");
                        checkpointTime = System.currentTimeMillis();
                        iterBytesReceived = 0;
                    }
                }
                fileOutputStream.close();

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;

                System.out.println("Received file: " + fileName);
                System.out.println("File size: " + fileSize + " bytes");
                System.out.println("Bytes received: " + totalBytesReceived + " bytes.");
                System.out.println("Speed of receiving data: " +
                        ServerUtils.convertToMBytesInSeconds(totalBytesReceived, totalTime) + " MBytes/s\n");
                success = totalBytesReceived == fileSize;

                outputStream = new DataOutputStream(clientSocket.getOutputStream());
                outputStream.writeBoolean(success);

                inputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (!clientSocket.isClosed())
                        clientSocket.close();
                    if (success) {
                        System.out.println("SUCCESS!");
                    }
                    else {
                        System.out.println("FAIL!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static FileOutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
        FileOutputStream fileOutputStream;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can't create upload folder.");
            }
        }

        File file = new File(dir, fileName);

        fileOutputStream = new FileOutputStream(file);
        return fileOutputStream;
    }
}