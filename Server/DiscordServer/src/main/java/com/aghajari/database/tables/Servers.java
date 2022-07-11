package com.aghajari.database.tables;

import com.aghajari.Main;
import com.aghajari.database.Connection;
import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.models.ServerModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Servers extends AbstractTable {
    private static final String INDEX = "index";
    private static final String NAME = "name";
    private static final String CHANNELS = "channels";
    private static final String OWNER = "owner";
    private static final String IMAGE = "image";
    private static final String PERMISSIONS = "permissions";
    private static final String SERVER_ID = "serverId";
    private static final String INVITE_CODE = "inviteCode";

    public Servers() {
        super("server");
    }

    public ServerModel insert(String owner, String name, String image) {
        try {
            synchronized (lock) {
                ServerModel model = new ServerModel(name, image, Main.rnd(), owner, 3, Main.rnd());

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(NAME, model.name);
                parameters.putString(IMAGE, model.avatar);
                parameters.putString(OWNER, model.owner);
                parameters.putString(SERVER_ID, model.serverId);
                parameters.putString(CHANNELS, "[]");
                parameters.putString(INVITE_CODE, model.inviteCode);
                parameters.putInt(PERMISSIONS, model.permissions);
                INSERT(parameters);
                return model;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String revoke(String serverId) {
        try {
            synchronized (lock) {
                String newCode = Main.rnd();

                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(SERVER_ID, serverId);
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(INVITE_CODE, newCode);
                UPDATE(parameters, whereParameters);
                return newCode;
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public boolean updateAvatar(String serverId, String image) {
        return update(serverId, IMAGE, image);
    }

    public boolean updateChannels(String serverId, String channels) {
        return update(serverId, CHANNELS, channels);
    }

    public boolean updateName(String serverId, String name) {
        return update(serverId, NAME, name);
    }

    public boolean updatePermissions(String serverId, int permissions) {
        return update(serverId, PERMISSIONS, permissions);
    }

    private <T> boolean update(String serverId, String key, T value) {
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(SERVER_ID, serverId);
                DatabaseParameters parameters = new DatabaseParameters();

                if (value instanceof String)
                    parameters.putString(key, (String) value);
                else
                    parameters.putInt(key, (Integer) value);

                UPDATE(parameters, whereParameters);
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    public boolean delete(String serverId) {
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(SERVER_ID, serverId);
                DELETE(whereParameters);
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    public ServerModel get(String serverId) {
        return get(SERVER_ID, serverId);
    }

    public ServerModel getByInviteCode(String inviteCode) {
        return get(INVITE_CODE, inviteCode);
    }

    private ServerModel get(String key, String value) {
        AtomicReference<ServerModel> server = new AtomicReference<>(null);
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(key, value);

                SELECT("*", whereParameters,
                        res -> {
                            if (res.next())
                                server.set(create(res));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return server.get();
    }

    public static ServerModel create(ResultSet set) throws SQLException {
        ServerModel model = new ServerModel(set.getString(SERVER_ID));
        model.owner = set.getString(OWNER);
        model.avatar = set.getString(IMAGE);
        model.name = set.getString(NAME);
        model.inviteCode = set.getString(INVITE_CODE);
        try {
            model.channels = new Gson().fromJson(set.getString(CHANNELS),
                    new TypeToken<ArrayList<ServerModel.ServerChannel>>() {
                    }.getType());
        } catch (Exception ignore) {
            model.channels = new ArrayList<>();
        }

        model.permissions = set.getInt(PERMISSIONS);
        return model;
    }
}

