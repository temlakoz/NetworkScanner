package com.example.networkscanner;

import com.example.networkscanner.scanner.PortScanner;
import com.example.networkscanner.scanner.ServiceVersionDetector;
import javafx.concurrent.Task;

import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetworkScanner {

    private ExecutorService executor;
    private long startTime;

    public Task<Void> startScan(InetAddress[] addresses, String portRangeInput, String threadsInput, boolean getServiceInfo, ScanResultHandler resultHandler) throws NumberFormatException {
        final int startPort;
        final int endPort;
        final int threadsNum = Integer.parseInt(threadsInput.trim());

        String[] ports = portRangeInput.split("-");
        startPort = Integer.parseInt(ports[0].trim());
        endPort = Integer.parseInt(ports[1].trim());


        executor = Executors.newFixedThreadPool(threadsNum);
        startTime = System.currentTimeMillis();

        return new Task<>() {
            @Override
            protected Void call() {
                for (InetAddress address : addresses) {
                    String finalAddress = address.getHostAddress();
                    for (int port = startPort; port <= endPort; port++) {
                        final int finalPort = port;
                        Runnable task = () -> {
                            StringBuilder result = new StringBuilder();
                            String isPortOpen = PortScanner.isPortOpen(finalAddress, finalPort, 200);
                            if (!isPortOpen.equals("")) {
                                result.append(isPortOpen);
                                if (getServiceInfo) {
                                    String version = ServiceVersionDetector.detectServiceVersion(finalAddress, finalPort, 1000);
                                    if (!Objects.equals(version, "Unknown")) {
                                        result.append(". Version: ").append(version);
                                    }
                                }
                                resultHandler.handleResult(result.toString());
                            }
                        };
                        executor.execute(task);
                    }
                }
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
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
