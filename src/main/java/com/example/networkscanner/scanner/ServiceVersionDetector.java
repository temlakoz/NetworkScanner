package com.example.networkscanner.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServiceVersionDetector {

    private static final Map<Integer, String> serviceMap = new HashMap<>();

    static {
        serviceMap.put(20, "FTP Data Transfer");
        serviceMap.put(21, "FTP Control");
        serviceMap.put(22, "SSH");
        serviceMap.put(23, "Telnet");
        serviceMap.put(25, "SMTP");
        serviceMap.put(53, "DNS");
        serviceMap.put(80, "HTTP");
        serviceMap.put(110, "POP3");
        serviceMap.put(111, "rpcbind");
        serviceMap.put(143, "IMAP");
        serviceMap.put(443, "HTTPS");
        serviceMap.put(465, "SMTPS");
        serviceMap.put(993, "IMAPS");
        serviceMap.put(995, "POP3S");
        serviceMap.put(3306, "MySQL");
        serviceMap.put(3389, "RDP");
        serviceMap.put(5432, "PostgreSQL");
        serviceMap.put(27017, "MongoDB");
        // Add more services and their default ports as needed
    }

    public static String getServiceName(int port) {
        return serviceMap.getOrDefault(port, "Unknown");
    }

    public static String detectServiceVersion(String ipAddress, int port, int timeout) {
        String serviceVersion = "";

        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(ipAddress, port), timeout);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream output = socket.getOutputStream();

            if (port == 80 || port == 443) {
                String httpRequest = "HEAD / HTTP/1.1\r\nHost: " + ipAddress + "\r\n\r\n";
                output.write(httpRequest.getBytes());
                output.flush();

                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    if (responseLine.startsWith("Server:")) {
                        serviceVersion = responseLine.substring(8).trim();
                        break;
                    }
                }
            } else {
                serviceVersion = reader.readLine().trim();
            }

        } catch (Exception e) {
            // Unknown
        }

        return serviceVersion.isEmpty() ? "Unknown" : serviceVersion;
    }

    private ServiceVersionDetector() {}
}
