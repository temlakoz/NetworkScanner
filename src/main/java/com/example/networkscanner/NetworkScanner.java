package com.example.networkscanner;

import com.example.networkscanner.scanner.PortScanner;
import com.example.networkscanner.scanner.ServiceVersionDetector;
import javafx.concurrent.Task;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class NetworkScanner {

    private ExecutorService executor;
    private long startTime;

    public Task<Void> startScan(InetAddress[] addresses, String portRangeInput, String threadsInput, boolean getServiceInfo, ScanResultHandler resultHandler) throws NumberFormatException {
        final int startPort;
        final int endPort;
        final int threadsNum = Integer.parseInt(threadsInput.trim());

        if (portRangeInput.contains("-")) {
            String[] ports = portRangeInput.split("-");
            startPort = Integer.parseInt(ports[0].trim());
            endPort = Integer.parseInt(ports[1].trim());
        } else if (!portRangeInput.isEmpty()) {
            startPort = 1;
            endPort = Integer.parseInt(portRangeInput.trim());
        } else {
            startPort = 1;
            endPort = 65535;
        }

        executor = Executors.newFixedThreadPool(threadsNum);
        startTime = System.currentTimeMillis();

        return new Task<>() {
            @Override
            protected Void call() {
                Arrays.stream(addresses).forEach(address -> {
                    String finalAddress = address.getHostAddress();
                    IntStream.rangeClosed(startPort, endPort).forEach(port -> {
                        Runnable task = () -> {
                            if (PortScanner.isPortOpen(finalAddress, port, 200)) {
                                String result = "IP: " + finalAddress + ". Port " + port + " is open.";
                                result += " Service: " + ServiceVersionDetector.getServiceName(port);
                                if (getServiceInfo) {
                                    String version = ServiceVersionDetector.detectServiceVersion(finalAddress, port, 1000);
                                    if (!Objects.equals(version, "Unknown")) {
                                        result += ". Version: " + version;
                                    }
                                }
                                resultHandler.handleResult(result);
                            }
                        };
                        executor.execute(task);
                    });
                });

                executor.shutdown();
                try {
                    // Wait until all tasks are finished.
                    while (!executor.isTerminated()) {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return null;
            }

            @Override
            protected void failed() {
                resultHandler.handleResult("Error: " + getException().getMessage());
            }
        };
    }
    public boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }

    public double getTotalTimeInSeconds() {
        return (System.currentTimeMillis() - this.startTime) / 1000.0;
    }
}
