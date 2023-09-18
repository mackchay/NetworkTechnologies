package ru.nsu.ccfit.haskov;

import java.net.*;

public class MulticastDiscoveryStart {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Error: lack of arguments");
            return;
        }

        String multicastGroup = args[0];
        InetAddress groupAddress;

        try {
            groupAddress = InetAddress.getByName(multicastGroup);
        } catch (UnknownHostException e) {
            System.out.println("False address of multicast.");
            return;
        }

        MulticastMode multicastMode;
        if (args[1].equals("--mode=receive")) {
            multicastMode = new MulticastReceiver();
        }
        else if (args[1].equals("--mode=send")) {
            multicastMode = new MulticastSender();
        }
        else {
            System.out.println("Error: incorrect mode format. Use '--mode=receive'" +
                    " or '--mode=send'");
            return;
        }

        int port = 12345;

        multicastMode.start(groupAddress, port);
    }
}
