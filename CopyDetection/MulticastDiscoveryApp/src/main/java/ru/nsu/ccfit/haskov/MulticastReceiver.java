package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulticastReceiver implements MulticastMode {

    private static final int DATA_SIZE = 1024;
    private static final int TIMEOUT = 5000;

    private final List<InetAddress> ipList = new ArrayList<>();

    @Override
    public void start(InetAddress groupAddress, int port) {
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, port);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(socketAddress, null);
            socket.setSoTimeout(TIMEOUT * 2);
            System.out.println("Ready for receive messages form copies...");
            byte[] receiveData = new byte[DATA_SIZE];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                InetAddress senderAddress = receivePacket.getAddress();
                if (!ipList.contains(senderAddress)) {
                    ipList.add(senderAddress);
                    executorService.execute(() -> sendBeaconFrame(socket, senderAddress, port));
                    printIpList();
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Waiting time is out.");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendBeaconFrame(MulticastSocket socket, InetAddress senderAddress, int port) {
        try {
            while (true) {
                byte[] sendData = "hi".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        senderAddress, port);
                socket.send(sendPacket);

                byte[] receiver = new byte[1024];
                DatagramPacket receivePack = new DatagramPacket(receiver, receiver.length);
                socket.setSoTimeout(TIMEOUT);
                socket.receive(receivePack);
            }
        } catch (Exception e) {
            ipList.remove(senderAddress);
            printIpList();
        }
    }

    private void printIpList() {
        System.out.print("Live copies list :");
        for (InetAddress ipAddress : ipList) {
            System.out.print(ipAddress.getHostAddress());
        }
        System.out.print("\n");
    }
}
