package com.example.networkscanner;
/**
 * Этот интерфейс определяет метод для обработки результатов сканирования.
 */

public interface ScanResultHandler {
    /**
     * Этот метод вызывается при обработке результата сканирования.
     *
     * @param result строка, содержащая результат сканирования
     */
    void handleResult(String result);
}
