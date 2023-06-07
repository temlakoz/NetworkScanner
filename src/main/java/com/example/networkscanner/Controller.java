package com.example.networkscanner;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

    private ScanService scanService;

    @FXML
    private void initialize() {
        scanService = new ScanService();
        scanButton.setOnAction(event -> handleScanButtonAction());
    }

    @FXML
    private void handleScanButtonAction() {
        resultArea.clear();

        String ipRange = ipRangeField.getText().trim();
        String portRangeInput = portRangeField.getText().trim();
        String threadsInput = threadsField.getText().trim();

        if (scanService.isRunning()) {
            appendResult("Error: Previous scan is still running.");
            return;
        }

        Task<Void> scanTask = scanService.startScan(ipRange, portRangeInput, threadsInput, serviceInfoCheckBox.isSelected(), this::appendResult);

        scanTask.setOnSucceeded(e -> {
            double totalTime = scanService.getTotalTimeInSeconds();
            appendResult("Scan completed in " + String.format("%.2f", totalTime) + " seconds.");
        });

        scanTask.setOnFailed(e -> appendResult("Error: " + e.getSource().getException().getMessage()));

        new Thread(scanTask).start();
    }

    private void appendResult(String result) {
        javafx.application.Platform.runLater(() -> resultArea.appendText(result + "\n"));
    }
}
