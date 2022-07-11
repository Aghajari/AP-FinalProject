package com.aghajari.database;

import com.aghajari.database.tables.*;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private static Connection instance = null;

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Discord";

    private static final String USER = "root";
    private static final String PASS = ":yX47Aq8]#z^WU}E";
    private java.sql.Connection conn = null;

    private final Friendship friendship = new Friendship();
    private final OpenedChats openedChats = new OpenedChats();
    private final Message message = new Message();
    private final Servers servers = new Servers();
    private final UserServers userServers = new UserServers();
    private final Blocked blocked = new Blocked();
    private final Bot bot = new Bot();

    public static Connection getInstance() {
        if (instance == null) {
            synchronized (JDBC_DRIVER) {
                if (instance == null) {
                    instance = new Connection();
                }
            }
        }

        return instance;
    }

    private Connection() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException {
        conn.close();
        instance = null;
    }

    public java.sql.Connection getConnection() {
        return conn;
    }

    public static Friendship getFriendship() {
        return getInstance().friendship;
    }

    public static OpenedChats getOpenedChats() {
        return getInstance().openedChats;
    }

    public static Message getMessage() {
        return getInstance().message;
    }

    public static Servers getServers() {
        return getInstance().servers;
    }

    public static UserServers getUserServers() {
        return getInstance().userServers;
    }

    public static Blocked getBlocked() {
        return getInstance().blocked;
    }

    public static Bot getBot() {
        return getInstance().bot;
    }
}
