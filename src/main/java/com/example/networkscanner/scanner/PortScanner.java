/**
 * Этот класс предоставляет метод для проверки открытости порта на конкретном IP-адресе и порту.
 *
 * @author Ваше_имя
 */
package com.example.networkscanner.scanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScanner {

    /**
     * Проверяет, открыт ли порт на указанном IP-адресе.
     *
     * @param ip IP-адрес для проверки
     * @param port номер порта для проверки
     * @param timeout время ожидания в миллисекундах
     * @return строку, указывающую, открыт ли порт, или пустую строку, если порт закрыт
     */
    public static String isPortOpen(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return "Port " + port + " is open on " + ip;
        } catch (IOException e) {
            return "";
        }
    }

    private PortScanner() {}
}
