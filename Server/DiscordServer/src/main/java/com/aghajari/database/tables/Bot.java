package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class Bot extends AbstractTable {

    private static final String INDEX = "index";
    private static final String CLIENT_ID = "client_id";
    private static final String LINK = "link";

    public Bot() {
        super("bot");
    }

    public void insert(String clientId, String link) {
        try {
            synchronized (lock) {
                delete(clientId);

                if (link == null || link.trim().isEmpty())
                    return;

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(CLIENT_ID, clientId);
                parameters.putString(LINK, link);
                INSERT(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String clientId) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(CLIENT_ID, clientId);
                DELETE(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String select(String clientId) {
        AtomicReference<String> model = new AtomicReference<>(null);
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(CLIENT_ID, clientId);

                SELECT(whereParameters, res -> {
                    if (res.next())
                        model.set(res.getString(LINK));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model.get();
    }

}
