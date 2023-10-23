package ru.nsu.ccfit.haskov;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", "port", true, "Server listening port.");
        CommandLine cmd;
        int port;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
        }
        else {
            throw new RuntimeException("Invalid arguments, it should contain: -p <port>");
        }
        TCPServer server = new TCPServer();
        server.start(port);
    }
}
