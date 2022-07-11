package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.models.OpenedChatModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Blocked extends AbstractTable {

    private static final String INDEX = "index";
    private static final String FROM_ID = "fromId";
    private static final String TO_ID = "toId";

    public Blocked() {
        super("blocked");
    }

    public void insert(String from, String to) {
        try {
            synchronized (lock) {
                delete(from, to);

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(FROM_ID, from);
                parameters.putString(TO_ID, to);
                INSERT(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int index) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putInt(INDEX, index);
                DELETE(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String from, String to) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(FROM_ID, from);
                parameters.putString(TO_ID, to);
                DELETE(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<OpenedChatModel> get(String from) {
        ArrayList<OpenedChatModel> openedChatsList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, from);

                SELECT(whereParameters, chats -> {
                    while (chats.next())
                        openedChatsList.add(create(chats));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return openedChatsList;
    }

    public boolean hasBlocked(String from, String to) {
        AtomicReference<Boolean> status = new AtomicReference<>(false);
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, from);
                whereParameters.putString(TO_ID, to);

                SELECT(whereParameters, set -> status.set(set.next()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status.get();
    }

    public int getBlockState(String from, String to) {
        if (hasBlocked(from, to))
            return 1;
        if (hasBlocked(to, from))
            return -1;
        return 0;
    }

    private static OpenedChatModel create(ResultSet set) throws SQLException {
        return new OpenedChatModel(set.getInt(INDEX), set.getString(TO_ID));
    }
}
