package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.models.MessageModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Message extends AbstractTable {
    private static final String INDEX = "message_index";
    private static final String FROM_ID = "fromId";
    private static final String TO_ID = "toId";
    private static final String TIMESTAMP = "timestamp";
    private static final String TYPE = "type";
    private static final String MESSAGE_TEXT = "message";
    private static final String DATA = "data";
    private static final String SEEN = "seen";
    private static final String EDITED = "edited";

    public Message() {
        super("message");
    }

    public int insert(MessageModel model) {
        AtomicReference<Integer> index = new AtomicReference<>(-1);
        try {
            synchronized (lock) {
                model.time = System.currentTimeMillis();

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(FROM_ID, model.fromId);
                parameters.putString(TO_ID, model.toId);
                parameters.putTimestamp(TIMESTAMP, model.time);
                parameters.putInt(TYPE, 1);
                parameters.putString(MESSAGE_TEXT, model.text);
                parameters.putString(DATA, "{}");
                parameters.putBoolean(SEEN, false);
                parameters.putBoolean(EDITED, false);
                INSERT(parameters, new String[]{INDEX}, set -> {
                    if (set.next())
                        index.set(model.index = set.getInt(1));
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index.get();
    }

    public MessageModel reaction(int message_index, String id, int reaction) {
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putInt(INDEX, message_index);
                MessageModel model = get(message_index);
                if (model != null) {
                    if (model.reactions == null)
                        model.reactions = new HashMap<>();
                    if (reaction == 0)
                        model.reactions.remove(id);
                    else
                        model.reactions.put(id, reaction);

                    DatabaseParameters parameters = new DatabaseParameters();
                    parameters.putString(DATA, new Gson().toJson(model.reactions));
                    UPDATE(parameters, whereParameters);
                    return model;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void seen(int message_index) {
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putInt(INDEX, message_index);

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putBoolean(SEEN, true);
                UPDATE(parameters, whereParameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public MessageModel get(int index) {
        AtomicReference<MessageModel> out = new AtomicReference<>(null);
        synchronized (lock) {
            DatabaseParameters whereParameters = new DatabaseParameters();
            whereParameters.putInt(INDEX, index);

            try {
                SELECT(whereParameters, messages -> {
                    if (messages.next())
                        out.set(create(messages));
                });
            } catch (Exception ignore) {
            }
        }
        return out.get();
    }

    public ArrayList<MessageModel> getSavedMessages(String toId) {
        ArrayList<MessageModel> messageList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, toId);
                whereParameters.putString(TO_ID, toId);
                SELECT("*", whereParameters,
                        messages -> {
                            while (messages.next())
                                messageList.add(create(messages));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public ArrayList<MessageModel> get(String targetId1, String targetId2) {
        ArrayList<MessageModel> messageList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, targetId1);
                whereParameters.putString(TO_ID, targetId1);
                whereParameters.putString(FROM_ID + 2, targetId2);
                whereParameters.putString(TO_ID + 2, targetId2);

                SELECT("*", whereParameters,
                        "(" + FROM_ID + "=? OR " + TO_ID + "=?) AND " +
                                "(" + FROM_ID + "=? OR " + TO_ID + "=?)",
                        messages -> {
                            while (messages.next())
                                messageList.add(create(messages));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public ArrayList<MessageModel> get(String toId) {
        ArrayList<MessageModel> messageList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(TO_ID, toId);
                SELECT("*", whereParameters,
                        messages -> {
                            while (messages.next())
                                messageList.add(create(messages));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public static MessageModel create(ResultSet set) throws SQLException {
        MessageModel model = new MessageModel();
        model.index = set.getInt(INDEX);
        model.fromId = set.getString(FROM_ID);
        model.toId = set.getString(TO_ID);
        model.time = set.getTimestamp(TIMESTAMP).getTime();
        model.text = set.getString(MESSAGE_TEXT);
        try {
            model.reactions = new Gson().fromJson(set.getString(DATA),
                    new TypeToken<HashMap<String, Integer>>() {
                    }.getType());
        } catch (Exception ignore) {
            model.reactions = null;
        }
        model.seen = set.getBoolean(SEEN);
        model.edited = set.getBoolean(EDITED);
        return model;
    }
}

