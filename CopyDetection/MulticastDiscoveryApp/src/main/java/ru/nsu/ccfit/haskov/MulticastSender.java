package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class MulticastSender extends Multicast {
    private static final int SENDING_PERIOD = 2000;
    @Override
    public void start(String key, InetAddress groupAddress, int port) {
        SocketAddress socketAddress = new InetSocketAddress(groupAddress, port);
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(socketAddress, null);
            System.out.println("Sending messages for other copies...");
            System.out.println("Press 'q' to stop sending");
            Timer timer = getTimer(key, socketAddress, socket);
            handlerQuit(timer);
            System.out.println("Sending closed.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Timer getTimer(String key, SocketAddress socketAddress, MulticastSocket socket) {
        byte[] sendData = key.getBytes(StandardCharsets.UTF_8);

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socketAddress);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                    try {
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }
        };
        timer.schedule(task, 0, SENDING_PERIOD);
        return timer;
    }
}
