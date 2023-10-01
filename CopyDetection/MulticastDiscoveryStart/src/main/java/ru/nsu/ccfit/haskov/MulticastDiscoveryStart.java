package ru.nsu.ccfit.haskov;

public class MulticastDiscoveryStart {

    private final static int PORT = 1234;

    public static void main(String[] args) {

        MulticastParser parser = new MulticastParser();
        MulticastOptions options = parser.parse(args);


        Multicast multicast = options.mode();
        multicast.start(
                options.key(),
                options.groupIp(),
                options.port()
        );
    }
}
