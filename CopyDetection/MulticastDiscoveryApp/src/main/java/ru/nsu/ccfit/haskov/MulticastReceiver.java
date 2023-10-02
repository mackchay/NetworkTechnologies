package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MulticastReceiver extends Multicast {

    private static final int DATA_SIZE = 1024;
    private static final int TIMEOUT = 5000;

    @Override
    public void start(String key, InetAddress groupAddress, int port) {
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, port);
        Set<InetAddress> ipList = new HashSet<>();

        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(socketAddress, null);
            System.out.println("Ready for receive messages form copies...");
            System.out.println("Press 'q' to stop receiving");
            Thread thread = new Thread(() -> {
                try {
                    receiveDatagram(key, socket, ipList);
                } catch (IOException e) {
                    socket.close();
                }
            });
            thread.start();
            TimerTask task = checkTimeout(ipList);
            Timer timer = new Timer();
            timer.schedule(task, 0, TIMEOUT);
            handlerQuit(timer);
            socket.close();
            timer.cancel();
            thread.interrupt();
            System.out.println("Receiver closed.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveDatagram(String key, MulticastSocket socket, Set<InetAddress> ipList) throws IOException {
        while (true) {
            byte[] receiveData = new byte[key.getBytes(StandardCharsets.UTF_8).length];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            InetAddress senderAddress = receivePacket.getAddress();
            if (Arrays.equals(receiveData, key.getBytes(StandardCharsets.UTF_8))) {
                ipList.add(senderAddress);
            }
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
