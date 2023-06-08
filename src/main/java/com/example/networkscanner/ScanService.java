package com.example.networkscanner;

import javafx.concurrent.Task;
import java.net.InetAddress;


public class ScanService {

    private final NetworkScanner networkScanner;

    public ScanService() {
        this.networkScanner = new NetworkScanner();
    }

    public double getTotalTimeInSeconds() {
        return networkScanner.getTotalTimeInSeconds();
    }

    public Task<Void> startScan(InetAddress[] addresses, String portRangeInput, String threadsInput, boolean getServiceInfo, ScanResultHandler resultHandler) {
        try {
            return networkScanner.startScan(addresses, portRangeInput, threadsInput, getServiceInfo, resultHandler);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return networkScanner.isRunning();
    }
}
