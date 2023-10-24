package org.example;

import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Устанавливаем соединение с сервером, который слушает на порту 12345
            Socket socket = new Socket("localhost", 12345);

            // Создаем читателя для входящих сообщений от сервера
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Создаем писателя для отправки сообщений серверу
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Scanner scr = new Scanner(System.in);

            while (true) {
                System.out.print("Введите логин: ");
                String login = scr.nextLine();
                System.out.print("Введите пароль: ");
                String password = scr.nextLine();
                System.out.println();

                writer.println(login + "," + password);
                String auth = reader.readLine();
                if (auth.equals("AUTH_SUCCESS")) {
                    System.out.println(auth);
                    break;
                } else {
                    System.out.println(auth);
                }
            }

            // Поток для чтения сообщений от сервера
            Thread readerThread = new Thread(() -> {
                try {
                    String message;
                    // Бесконечный цикл для чтения сообщений от сервера
                    while ((message = reader.readLine()) != null) {
                        System.out.println("Сервер: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            // Чтение сообщений с консоли и отправка на сервер
            BufferedReader consoleReader = new BufferedReader(
                    new InputStreamReader(System.in));

            String userInput;
            // Бесконечный цикл для чтения сообщений с консоли и отправки на сервер
            while ((userInput = consoleReader.readLine()) != null) {
                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
