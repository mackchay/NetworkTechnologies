package ru.nsu.ccfit.haskov;

import java.net.InetAddress;

public record MulticastOptions(String key, int port, InetAddress groupIp, Multicast mode) {

}
