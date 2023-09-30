package ru.nsu.ccfit.haskov;

public class MulticastParser {

    public MulticastMode parseMode(String mode) {
        MulticastMode multicastMode;
        if (mode.equals("--mode=receive")) {
            multicastMode = new MulticastReceiver();
        }
        else if (mode.equals("--mode=send")) {
            multicastMode = new MulticastSender();
        }
        else {
            throw new IllegalArgumentException();
        }
        return multicastMode;
    }
}
