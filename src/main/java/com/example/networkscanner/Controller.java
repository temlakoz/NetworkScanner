/**
 * Этот класс служит контроллером для приложения Network Port Scanner.
 * Он управляет взаимодействием между пользовательским интерфейсом и службой сканирования.
 */
package com.example.networkscanner;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    /**
     * Этот метод инициализирует контроллер после загрузки всей разметки.
     * Он создает новую службу сканирования и устанавливает обработчик событий для кнопки сканирования.
     */
    @FXML
    private void initialize() {
        scanService = new ScanService();
        scanButton.setOnAction(event -> {
            try {
                handleScanButtonAction();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Этот метод обрабатывает действие кнопки сканирования.
     * Он считывает и проверяет ввод, а затем запускает задачу сканирования.
     *
     * @throws UnknownHostException если указанный диапазон IP-адресов неверен
     */
    @FXML
    private void handleScanButtonAction() throws UnknownHostException {

        String ipRange = ipRangeField.getText().trim();
        String portRangeInput = portRangeField.getText().trim();
        String threadsInput = threadsField.getText().trim();

        if (scanService.isRunning()) {
            appendResult("Ошибка: предыдущее сканирование ещё не завершено.");
            return;
        }

        resultArea.clear();

        // Проверка ввода
        InetAddress[] addresses;
        try {
            addresses = InputValidator.validateIpRange(ipRange);
            InputValidator.validatePortRangeInput(portRangeInput);
            InputValidator.validateThreadsInput(threadsInput);
        } catch (IllegalArgumentException | UnknownHostException e) {
            appendResult("Ошибка: " + e.getMessage());
            return;
        }

        // Запуск задачи сканирования
        Task<Void> scanTask = scanService.startScan(addresses, portRangeInput, threadsInput, serviceInfoCheckBox.isSelected(), this::appendResult);
        scanTask.setOnSucceeded(e -> {
            double totalTime = scanService.getTotalTimeInSeconds();
            appendResult("Сканирование завершено за " + String.format("%.2f", totalTime) + " секунд.");
        });
        scanTask.setOnFailed(e -> appendResult("Error: " + e.getSource().getException().getMessage()));

        new Thread(scanTask).start();
    }

    /**
     * Этот метод добавляет сообщение в область вывода результата.
     * Он гарантирует, что вызов происходит в потоке JavaFX, чтобы не было проблем с многопоточностью.
     *
     * @param result сообщение для добавления в область вывода
     */
    private void appendResult(String result) {
        javafx.application.Platform.runLater(() -> resultArea.appendText(result + "\n"));
    }
}