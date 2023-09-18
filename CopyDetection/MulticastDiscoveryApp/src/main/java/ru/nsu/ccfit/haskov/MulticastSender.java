package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;

public class MulticastSender implements MulticastMode {

    @Override
    public void start(InetAddress groupAddress, int port) {
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, port);
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(socketAddress, null);
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            String processName = runtimeMxBean.getName();
            System.out.println("Sending messages for other copies...");

            byte[] sendData = ("Hello, I'm " + processName).getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socketAddress);
            while (true) {
                socket.send(sendPacket);
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
