package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulticastReceiver implements MulticastMode {

    private static final int DATA_SIZE = 1024;
    private static final int TIMEOUT = 5000;

    @Override
    public void start(InetAddress groupAddress, int port) {
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, port);
        Set<InetAddress> ipList = new HashSet<>();

        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(socketAddress, null);
            System.out.println("Ready for receive messages form copies...");
            Thread thread = new Thread(() -> {
                try {
                    receiveDatagram(socket, ipList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            TimerTask task = checkTimeout(ipList);
            Timer timer = new Timer();
            timer.schedule(task, 0, TIMEOUT);
            thread.join();

        } catch (SocketTimeoutException e) {
            System.out.println("Waiting time is out.");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveDatagram(MulticastSocket socket, Set<InetAddress> ipList) throws IOException {
        while (true) {
            byte[] receiveData = new byte[DATA_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            InetAddress senderAddress = receivePacket.getAddress();
            ipList.add(senderAddress);
        }
    }

    private TimerTask checkTimeout(Set<InetAddress> ipList) {
        Set<InetAddress> ipListVerified = new HashSet<>();
        return new TimerTask() {
            @Override
            public void run() {
                if (!ipList.equals(ipListVerified)) {
                    ipListVerified.clear();
                    ipListVerified.addAll(ipList);
                    System.out.println("Live copies list :" + ipList);
                }
                ipList.clear();
            }
        };
    }

}
