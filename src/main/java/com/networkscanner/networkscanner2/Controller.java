package com.networkscanner.networkscanner2;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.networkscanner.networkscanner2.scanner.PortScanner;
import com.networkscanner.networkscanner2.scanner.ServiceVersionDetector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Controller {

    @FXML
    private TextField ipRangeField;

    @FXML
    private TextField portRangeField;

    @FXML
    private TextField threadsField;

    @FXML
    private CheckBox serviceInfoCheckBox;

    @FXML
    private Button scanButton;

    @FXML
    private TextArea resultArea;

    private ExecutorService executor;

    @FXML
    private void initialize() {
        scanButton.setOnAction(event -> handleScanButtonAction());
    }

    @FXML
    private void handleScanButtonAction() {
        resultArea.clear();

        String ipRange = ipRangeField.getText().trim();
        String portRangeInput = portRangeField.getText().trim();
        String threadsInput = threadsField.getText().trim();
        if (executor != null && !executor.isShutdown()) {
            appendResult("Error: Previous scan is still running.");
            return;
        }

        try {
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

            if (endPort < startPort || startPort < 1 || endPort > 65535) {
                appendResult("Error: Invalid port range.");
                return;
            }

            final InetAddress[] addresses = InetAddress.getAllByName(ipRange);
            final boolean getServiceInfo = serviceInfoCheckBox.isSelected();
            executor = Executors.newFixedThreadPool(threadsNum);

            long startTime = System.currentTimeMillis();

            Task<Void> scanTask = new Task<>() {
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
                                    appendResult(result);
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
                protected void succeeded() {
                    long endTime = System.currentTimeMillis();
                    double totalTime = (endTime - startTime) / 1000.0;
                    DecimalFormat df = new DecimalFormat("#.00");
                    appendResult("Scan completed in " + df.format(totalTime) + " seconds.");
                }

                @Override
                protected void failed() {
                    appendResult("Error: " + getException().getMessage());
                }
            };

            new Thread(scanTask).start();

        } catch (NumberFormatException e) {
            appendResult("Error: Invalid port number.");
        } catch (UnknownHostException e) {
            appendResult("Error: " + e.getMessage());
        }
    }

    private void appendResult(String result) {
        javafx.application.Platform.runLater(() -> resultArea.appendText(result + "\n"));
    }
}
