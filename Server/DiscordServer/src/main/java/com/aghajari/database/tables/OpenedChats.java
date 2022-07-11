package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.IDModel;
import com.aghajari.shared.models.OpenedChatModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class OpenedChats extends AbstractTable {

    private static final String INDEX = "opened_chats_index";
    private static final String OWNER_ID = "ownerId";
    private static final String USER_ID = "userId";

    public OpenedChats() {
        super("opened_chats");
    }

    public void insert(String ownerId, String userId) {
        try {
            synchronized (lock) {
                delete(ownerId, userId);

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(OWNER_ID, ownerId);
                parameters.putString(USER_ID, userId);
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

    public void delete(String ownerId, String userId) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(OWNER_ID, ownerId);
                parameters.putString(USER_ID, userId);
                DELETE(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<OpenedChatModel> get(String ownerId) {
        ArrayList<OpenedChatModel> openedChatsList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(OWNER_ID, ownerId);

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

    public ArrayList<IDModel> getOwners(String userId) {
        ArrayList<IDModel> openedChatsList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(USER_ID, userId);

                SELECT(whereParameters, chats -> {
                    while (chats.next())
                        openedChatsList.add(new IDModel(chats.getString(OWNER_ID)));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return openedChatsList;
    }

    public OpenedChatModel select(String ownerId, String userId) {
        AtomicReference<OpenedChatModel> model = new AtomicReference<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(OWNER_ID, ownerId);
                whereParameters.putString(USER_ID, userId);

                SELECT(whereParameters, chats -> {
                    if (chats.next())
                        model.set(create(chats));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model.get();
    }

    private static OpenedChatModel create(ResultSet set) throws SQLException {
        return new OpenedChatModel(set.getInt(INDEX), set.getString(USER_ID));
    }
}
