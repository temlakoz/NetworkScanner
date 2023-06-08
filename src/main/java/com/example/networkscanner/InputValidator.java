/**
 * Этот класс предоставляет статические методы для проверки ввода пользователя.
 * Он проверяет диапазоны IP-адресов, диапазоны портов и количество потоков.
 */
package com.example.networkscanner;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InputValidator {
    /**
     * Этот метод проверяет диапазон IP-адресов на правильность.
     *
     * @param ipRange строка, содержащая диапазон IP-адресов
     * @return массив IP-адресов, соответствующих диапазону
     * @throws UnknownHostException если указанный диапазон IP-адресов неверен
     */
    public static InetAddress[] validateIpRange(String ipRange) throws UnknownHostException {
        if (ipRange.isEmpty()) {
            throw new IllegalArgumentException("Поле IP адреса не может быть пустым.");
        }

        return InetAddress.getAllByName(ipRange);
    }

    /**
     * Этот метод проверяет диапазон портов на правильность.
     *
     * @param portRangeInput строка, содержащая диапазон портов
     */
    public static void validatePortRangeInput(String portRangeInput) {
        if (portRangeInput.isEmpty()) {
            throw new IllegalArgumentException("Поле портов не может быть пустым.");
        }

        String[] ports = portRangeInput.split("-");
        if (ports.length != 2) {
            throw new IllegalArgumentException("Поле порта должно быть заполнено в формате начало-конец.");
        }

        int startPort;
        int endPort;
        try {
            startPort = Integer.parseInt(ports[0]);
            endPort = Integer.parseInt(ports[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Номера портов должны быть числами.");
        }

        if (startPort < 1 || startPort > 65535 || endPort < 1 || endPort > 65535) {
            throw new IllegalArgumentException("Порты должны быть в диапазоне 1-65535.");
        }

        if (startPort > endPort) {
            throw new IllegalArgumentException("Начальный порт должен быть меньше конечного.");
        }
    }

    /**
     * Этот метод проверяет количество потоков на правильность.
     *
     * @param threadsInput строка, содержащая количество потоков
     */
    public static void validateThreadsInput(String threadsInput) {
        if (threadsInput.isEmpty()) {
            throw new IllegalArgumentException("Поле количества потоков не может быть пустым.");
        }

        int threads = Integer.parseInt(threadsInput);
        if (threads < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть больше чем 0.");
        }
    }

    private InputValidator() {}
}
