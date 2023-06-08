/**
 * Этот класс предоставляет методы для запуска и управления задачей сканирования.
 * Он оборачивает NetworkScanner и предоставляет удобный интерфейс для контроллера.
 */
package com.example.networkscanner;

import javafx.concurrent.Task;
import java.net.InetAddress;


public class ScanService {

    private final NetworkScanner networkScanner;

    /**
     * Создает новый объект ScanService и инициализирует NetworkScanner.
     */
    public ScanService() {
        this.networkScanner = new NetworkScanner();
    }

    /**
     * Получает общее время выполнения последнего сканирования в секундах.
     *
     * @return общее время выполнения последнего сканирования в секундах
     */
    public double getTotalTimeInSeconds() {
        return networkScanner.getTotalTimeInSeconds();
    }

    /**
     * Запускает задачу сканирования с заданными параметрами.
     *
     * @param addresses массив IP-адресов для сканирования
     * @param portRangeInput строка, содержащая диапазон портов для сканирования
     * @param threadsInput строка, содержащая количество потоков для выполнения сканирования
     * @param getServiceInfo логическое значение, указывающее, следует ли получать информацию о сервисе
     * @param resultHandler обработчик результатов сканирования
     * @return задача, которую можно запустить в новом потоке
     */
    public Task<Void> startScan(InetAddress[] addresses, String portRangeInput, String threadsInput, boolean getServiceInfo, ScanResultHandler resultHandler) {
        try {
            return networkScanner.startScan(addresses, portRangeInput, threadsInput, getServiceInfo, resultHandler);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Проверяет, выполняется ли задача сканирования.
     *
     * @return логическое значение, указывающее, выполняется ли задача сканирования
     */
    public boolean isRunning() {
        return networkScanner.isRunning();
    }
}
