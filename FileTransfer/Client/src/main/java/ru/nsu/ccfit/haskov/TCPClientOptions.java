package ru.nsu.ccfit.haskov;

import java.net.InetAddress;
import java.nio.file.Path;

public record TCPClientOptions (Path path, int port, InetAddress inetAddress, int speed) {

}
