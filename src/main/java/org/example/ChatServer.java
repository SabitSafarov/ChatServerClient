package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    // Список писателей для всех клиентов
    private static final List<PrintWriter> clients = new ArrayList<>();
    private static Map<String, String> loginAndPassword = new HashMap<>();

    public static void main(String[] args) {
        usersInfoLoad();

        try {
            // Создаем серверный сокс, который слушает на порту 12345
            ServerSocket serverSocket = new ServerSocket(12345);

            while (true) {
                System.out.println("Ожидание подключения клиента...");
                // Принимаем клиентское соединение
                Socket clientSocket = serverSocket.accept();

                // Создаем писателя для отправки сообщений клиенту
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);

                BufferedReader authReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                boolean run = true;
                while (run) {
                    String auth = authReader.readLine();
                    for (Map.Entry<String, String> user : loginAndPassword.entrySet()) {
                        if ((user.getKey() + "," + user.getValue()).equals(auth)) {
                            writer.println("AUTH_SUCCESS");
                            run = false;
                            break;
                        }
                    }
                    if (run) {
                        writer.println("AUTH_FAIL");
                    }
                }

                System.out.println("Клиент подключен.");

                // Поток для чтения сообщений от клиента
                Thread clientThread = new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));

                        String message;
                        // Бесконечный цикл для чтения сообщений от клиента
                        while ((message = reader.readLine()) != null) {
                            System.out.println("Получено от клиента: " + message);
                            broadcastMessage(message); // Рассылка сообщения всем клиентам
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для рассылки сообщения всем клиентам
    private static void broadcastMessage(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }
    private static void usersInfoLoad() {
        try (Reader reader = new FileReader("src/main/resources/users.txt");
             Scanner scr = new Scanner(reader)){
            scr.nextLine();

            while (scr.hasNext()) {
                String[] array = scr.nextLine().split(",");
                loginAndPassword.put(array[0], array[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
