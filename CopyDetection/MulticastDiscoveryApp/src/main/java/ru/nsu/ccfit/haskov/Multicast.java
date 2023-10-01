package ru.nsu.ccfit.haskov;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

public abstract class Multicast {
    public abstract void start(String key, InetAddress groupAddress, int port);

    public void handlerQuit(Timer timer) throws IOException {
        while (true) {
            int keyButton = System.in.read();
            if (keyButton == 'q') {
                timer.cancel();
                break;
            }
        }
    }
}
