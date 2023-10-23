package ru.nsu.ccfit.haskov;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;


public class TCPClientParser {
    private final Options options = new Options();
    private final String example = "Usage: -f <file> -i <ip-address> -p <port> -s <speed>";
    public TCPClientParser() {
        options.addOption("f", "file", true, "Path to file.");
        options.addOption("p", "port", true, "Server port.");
        options.addOption("i", "ip", true, "Server ip address.");
        options.addOption("s", "speed", true, "Connection speed MB/s");
    }

    public TCPClientOptions parse(String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Path path;
        int port;
        InetAddress ip;
        int speed;

        if (cmd.hasOption("f")) {
            path = Path.of(cmd.getOptionValue("f"));
        }
        else {
            throw new RuntimeException("ERROR: there is no -f <file> option.\n" + example);
        }
        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
        }
        else {
            throw new RuntimeException("ERROR: there is no -p <port> option.\n" + example);
        }
        if (cmd.hasOption("i")) {
            try {
                ip = InetAddress.getByName(cmd.getOptionValue("i"));
            } catch (UnknownHostException e) {
                throw new RuntimeException("ERROR: wrong format of ip address.\n" + example);
            }
        }
        else {
            throw new RuntimeException("ERROR: there is no -i <ip> option.\n" + example);
        }
        if (cmd.hasOption("s")) {
            speed = Integer.parseInt(cmd.getOptionValue("s"));
        }
        else {
            throw new RuntimeException("ERROR: there is no -s <speed> option.\n" + example);
        }
        return new TCPClientOptions(path, port, ip, speed);
    }
}
