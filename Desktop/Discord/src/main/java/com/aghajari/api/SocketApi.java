package com.aghajari.api;

import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.store.SocketListeners;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

public class SocketApi {

    private static SocketApi instance;

    public static SocketApi getInstance() {
        if (instance == null || (!instance.isConnected() &&
                System.currentTimeMillis() - instance.connectionTime > 2000)) {
            instance = new SocketApi();
            SocketListeners.init();
        }
        return instance;
    }

    public static void closeSocket() {
        if (instance != null && instance.isConnected())
            instance.close();
    }

    private Socket socket;
    private final Hashtable<Integer, HashMap<String, LinkedList<Listener>>>
            listeners = new Hashtable<>();
    private boolean connected;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private final InputHandler inputHandler;
    private final long connectionTime;

    private SocketApi() {
        connectionTime = System.currentTimeMillis();
        inputHandler = new InputHandler();
        try {
            socket = new Socket(ApiService.SOCKET_IP, ApiService.SOCKET_PORT);
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());
            connected = true;
            inputHandler.start();
        } catch (Exception e) {
            connected = false;
        }
    }

    public void close() {
        try {
            connected = false;
            socket.close();
            if (inputHandler != null)
                inputHandler.interrupt();
        } catch (IOException ignore) {
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void register(int type, Listener listener) {
        register(type, null, listener);
    }

    public void clear(int type) {
        listeners.remove(type);
    }

    public void register(String type, Listener listener) {
        register(SocketEvents.API, type, listener);
    }

    public void register(int type, String name, Listener listener) {
        if (listeners.containsKey(type)) {
            HashMap<String, LinkedList<Listener>> map = listeners.get(type);
            if (!map.containsKey(name))
                map.put(name, new LinkedList<>());
            map.get(name).add(listener);
        } else {
            HashMap<String, LinkedList<Listener>> map = new HashMap<>();
            map.put(null, new LinkedList<>());
            if (name != null)
                map.put(name, new LinkedList<>());
            map.get(name).add(listener);
            listeners.put(type, map);
        }
    }

    public void unregister(Listener listener) {
        for (HashMap<String, LinkedList<Listener>> l : listeners.values()) {
            for (LinkedList<Listener> l2 : l.values()) {
                l2.remove(listener);
            }
        }
    }

    private final Object lock = new Object();

    public boolean write(SocketModel model) {
        if (!connected)
            return false;

        synchronized (lock) {
            try {
                writer.writeObject(model);
                return true;
            } catch (IOException ignore) {
            }
            return false;
        }
    }

    public interface Listener {
        void onReceive(SocketModel model);
    }

    class InputHandler extends Thread {

        boolean running = true;

        @Override
        public void run() {
            super.run();

            while (running) {
                try {
                    SocketModel model = (SocketModel) reader.readObject();
                    HashMap<String, LinkedList<Listener>> l = listeners.get(model.eventType);
                    if (l != null) {
                        LinkedList<Listener> list = l.get(null);
                        if (list != null)
                            for (Listener listener : list)
                                listener.onReceive(model);

                        if (model.name != null) {
                            list = l.get(model.name);
                            if (list != null)
                                for (Listener listener : list)
                                    listener.onReceive(model);
                        }
                    }
                } catch (Exception ignore) {
                    break;
                }
            }
            connected = false;
            try {
                socket.close();
            } catch (IOException ignore) {
            }
            System.out.println("Closed");
        }
    }

}
