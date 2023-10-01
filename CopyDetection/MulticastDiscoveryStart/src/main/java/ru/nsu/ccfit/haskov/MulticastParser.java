package ru.nsu.ccfit.haskov;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.cli.*;

public class MulticastParser {

    private static final Options options = new Options();

    public MulticastParser() {
        options.addOption("i", "ip", true, "multicast ip address.");
        options.addOption("p", "port", true, "app port.");
        options.addOption("k", "key", true, "unique key message for app.");
        options.addOption("m", "mode", true, "app mode: receiver or sender.");
    }
    public MulticastOptions parse(String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String key;
        int port;
        InetAddress address;
        Multicast mode;

        if (cmd.hasOption("k")) {
            key = cmd.getOptionValue("k");
        }
        else {
            throw new RuntimeException("ERROR: there is no -k (-key) option");
        }

        if (cmd.hasOption("i")) {
            try {
                address = InetAddress.getByName(cmd.getOptionValue("i"));
            } catch (UnknownHostException e) {
                throw new RuntimeException("ERROR: option -ip: illegal name of ip address.");
            }
        }
        else {
            throw new RuntimeException("ERROR: there is no -i (-ip) option");
        }

        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
        } else {
            throw new RuntimeException("ERROR: there is no -p (-port) option");
        }

        if (cmd.hasOption("m")) {
            mode = parseMode(cmd.getOptionValue("m"));
        }
        else {
            throw new RuntimeException("ERROR: there is no -m (-mode) option");
        }

        return new MulticastOptions(key, port, address, mode);
    }
    public Multicast parseMode(String mode) {
        Multicast multicast;
        if (mode.equals("recv")) {
            multicast = new MulticastReceiver();
        }
        else if (mode.equals("send")) {
            multicast = new MulticastSender();
        }
        else {
            throw new RuntimeException("ERROR: option -mode: illegal name of mode.");
        }
        return multicast;
    }
}
