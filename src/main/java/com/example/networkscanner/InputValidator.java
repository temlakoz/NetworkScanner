package com.example.networkscanner;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InputValidator {

    public static void validateIpRange(String ipRange) throws UnknownHostException {
        if (ipRange.isEmpty()) {
            throw new IllegalArgumentException("IP range cannot be empty.");
        }
        // Проверка, что IP-адрес корректен
        InetAddress.getAllByName(ipRange);
    }

    public static void validatePortRangeInput(String portRangeInput) {
        if (portRangeInput.isEmpty()) {
            throw new IllegalArgumentException("Port range cannot be empty.");
        }

        String[] ports = portRangeInput.split("-");
        if (ports.length != 2) {
            throw new IllegalArgumentException("Port range must be in the format start-end.");
        }

        int startPort;
        int endPort;
        try {
            startPort = Integer.parseInt(ports[0]);
            endPort = Integer.parseInt(ports[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port numbers must be integers.");
        }

        if (startPort < 1 || startPort > 65535 || endPort < 1 || endPort > 65535) {
            throw new IllegalArgumentException("Port numbers must be in the range 1-65535.");
        }

        if (startPort > endPort) {
            throw new IllegalArgumentException("The start port must be less than or equal to the end port.");
        }
    }


    public static void validateThreadsInput(String threadsInput) {
        if (threadsInput.isEmpty()) {
            throw new IllegalArgumentException("Threads input cannot be empty.");
        }

        int threads = Integer.parseInt(threadsInput);
        if (threads < 1) {
            throw new IllegalArgumentException("The number of threads must be greater than 0.");
        }
    }
}
