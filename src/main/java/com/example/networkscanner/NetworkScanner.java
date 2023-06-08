/**
 * Класс NetworkScanner отвечает за сканирование диапазона IP-адресов на указанный диапазон портов.
 * Сканирование выполняется в несколько потоков, число которых указывается при запуске сканирования.
 */
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

    /**
     * Метод startScan запускает сканирование диапазона IP-адресов на указанный диапазон портов.
     *
     * @param addresses      Массив IP-адресов для сканирования.
     * @param portRangeInput Строка с диапазоном портов для сканирования.
     * @param threadsInput   Строка с числом потоков для сканирования.
     * @param getServiceInfo Флаг, указывающий, следует ли получать информацию о службах.
     * @param resultHandler  Обработчик результатов сканирования.
     * @return Task<Void>, который при выполнении запускает сканирование.
     * @throws NumberFormatException если строки portRangeInput или threadsInput не могут быть преобразованы в числа.
     */
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
                        Runnable task = createScanTask(finalAddress, port, getServiceInfo, resultHandler);
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
                resultHandler.handleResult("Ошибка: " + getException().getMessage());
            }
        };
    }

    /**
     * Метод isRunning возвращает true, если в данный момент выполняется сканирование, и false в противном случае.
     *
     * @return boolean - статус исполнения.
     */
    public boolean isRunning() {
        return executor != null && !executor.isTerminated();
    }

    /**
     * Метод getTotalTimeInSeconds возвращает общее время сканирования в секундах.
     *
     * @return double - общее время сканирования в секундах.
     */
    public double getTotalTimeInSeconds() {
        return (System.currentTimeMillis() - this.startTime) / 1000.0;
    }

    /**
     * Метод createScanTask создает задачу для сканирования одного порта на одном адресе.
     *
     * @param address        IP-адрес для сканирования.
     * @param port           Порт для сканирования.
     * @param getServiceInfo Флаг, указывающий, следует ли получать информацию о службах.
     * @param resultHandler  Обработчик результатов сканирования.
     * @return Runnable задачу, которую можно выполнить в отдельном потоке.
     */
    private Runnable createScanTask(String address, int port, boolean getServiceInfo, ScanResultHandler resultHandler) {
        return () -> {
            StringBuilder result = new StringBuilder();
            String isPortOpen = PortScanner.isPortOpen(address, port, 200);
            if (!isPortOpen.equals("")) {
                result.append(isPortOpen);
                if (getServiceInfo) {
                    String version = ServiceVersionDetector.detectServiceVersion(address, port, 1000);
                    if (!Objects.equals(version, "Unknown")) {
                        result.append(version);
                    }
                }
                resultHandler.handleResult(result.toString());
            }
        };
    }
}
