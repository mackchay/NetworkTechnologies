package ru.nsu.ccfit.haskov;

import java.net.InetAddress;

public interface MulticastMode {
    public void start(InetAddress groupAddress, int port);
}
