package com.aghajari;

import com.aghajari.api.ApiService;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket socketServer = new ServerSocket(ApiService.SOCKET_PORT);

        while (true) {
            try {
                new UserThread(socketServer.accept()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final Random rnd = new SecureRandom();
    private static final char[] symbols = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789").toCharArray();

    public static String rnd() {
        StringBuilder builder = new StringBuilder(12);
        for (int idx = 0; idx < 12; ++idx)
            builder.append(symbols[rnd.nextInt(symbols.length)]);
        return builder.toString();
    }

}
