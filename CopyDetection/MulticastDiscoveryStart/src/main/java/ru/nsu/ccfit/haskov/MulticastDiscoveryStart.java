package ru.nsu.ccfit.haskov;

import java.net.*;

public class MulticastDiscoveryStart {

    private final static int PORT = 1234;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Error: lack of arguments");
            return;
        }

        try {
            MulticastParser parser = new MulticastParser();
            String multicastGroup = args[0];
            int port = Integer.parseInt(args[1]);
            String mode = args[2];

            InetAddress groupAddress;
            groupAddress = InetAddress.getByName(multicastGroup);
            MulticastMode multicastMode = parser.parseMode(mode);
            multicastMode.start(groupAddress, port);
        } catch (NumberFormatException e) {
            System.out.println("False format of port.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: incorrect mode format. Use '--mode=receive'" +
                    " or '--mode=send'");
        } catch (UnknownHostException e) {
            System.out.println("False address of multicast.");
        }
    }
}
