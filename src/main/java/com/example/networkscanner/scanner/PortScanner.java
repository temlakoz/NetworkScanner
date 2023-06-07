package com.example.networkscanner.scanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class PortScanner implements Callable<String> {

    private final String ip;
    private final int port;

    public PortScanner(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static boolean isPortOpen(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String call() throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 200);
            return "Port " + port + " is open on " + ip;
        } catch (IOException e) {
            return "Port " + port + " is closed on " + ip;
        }
    }
}
