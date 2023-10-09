package ru.nsu.ccfit.haskov;

public class Main {
    public static void main(String[] args) {
        TCPClientParser parser = new TCPClientParser();
        TCPClientOptions options = parser.parse(args);
        TCPClient client = new TCPClient();
        client.sendFile(options);
    }
}