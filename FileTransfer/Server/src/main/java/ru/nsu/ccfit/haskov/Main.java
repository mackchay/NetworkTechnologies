package ru.nsu.ccfit.haskov;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Invalid arguments, it should contain: <port>");
        }
        int port = Integer.parseInt(args[0]);
        Server server = new Server();
        server.start(port);
    }
}
