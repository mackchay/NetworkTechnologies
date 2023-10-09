package ru.nsu.ccfit.haskov;

public class ServerUtils {

    public static double convertToSeconds(long milliseconds) {
        return milliseconds / 1000.0;
    }

    public static double convertToMBytes(long bytes) {
        return bytes / Math.pow(2.0, 20);
    }

    public static double convertToMBytesInSeconds(long bytes, long milliseconds) {
        return convertToMBytes(bytes) / convertToSeconds(milliseconds);
    }
}
