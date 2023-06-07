package com.example.networkscanner;

import javafx.concurrent.Task;
import java.net.UnknownHostException;

public class ScanService {

    private final NetworkScanner networkScanner;

    public ScanService() {
        this.networkScanner = new NetworkScanner();
    }

    public double getTotalTimeInSeconds() {
        return networkScanner.getTotalTimeInSeconds();
    }

    public Task<Void> startScan(String ipRange, String portRangeInput, String threadsInput, boolean getServiceInfo, ScanResultHandler resultHandler) {
        try {
            return networkScanner.startScan(ipRange, portRangeInput, threadsInput, getServiceInfo, resultHandler);
        } catch (NumberFormatException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return networkScanner.isRunning();
    }
}
