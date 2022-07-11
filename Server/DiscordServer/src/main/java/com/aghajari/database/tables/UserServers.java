package com.aghajari.database.tables;

import com.aghajari.database.Connection;
import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.models.IDModel;
import com.aghajari.shared.models.ServerModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class UserServers extends AbstractTable {
    private static final String INDEX = "id";
    private static final String SERVER_ID = "server_id";
    private static final String USER_ID = "user_id";
    private static final String PERMISSIONS = "permissions";

    public UserServers() {
        super("user_servers");
    }

    public void insert(String owner, String server) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, server);
                parameters.putString(USER_ID, owner);
                parameters.putInt(PERMISSIONS, 0);
                INSERT(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteAll(String serverId) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, serverId);
                DELETE(parameters);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String serverId, String userId) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, serverId);
                parameters.putString(USER_ID, userId);
                DELETE(parameters);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String owner, String serverId) {
        AtomicReference<Boolean> ref = new AtomicReference<>(false);
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(USER_ID, owner);
                parameters.putString(SERVER_ID, serverId);
                SELECT(parameters, set -> ref.set(set.next()));
            }
        } catch (Exception ignore) {
        }
        return ref.get();
    }

    public ArrayList<IDModel> select(String serverId) {
        ArrayList<IDModel> ids = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, serverId);
                SELECT(parameters, set -> {
                    while (set.next())
                        ids.add(new IDModel(set.getString(USER_ID)));
                });
            }
        } catch (Exception ignore) {
        }
        return ids;
    }

    public int getPermission(String serverId, String userId) {
        AtomicReference<Integer> p = new AtomicReference<>(0);
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, serverId);
                parameters.putString(USER_ID, userId);
                SELECT(parameters, set -> {
                    if (set.next())
                        p.set(set.getInt(PERMISSIONS));
                });
            }
        } catch (Exception ignore) {
        }
        return p.get();
    }

    public boolean updateMemberPermission(String serverId, String userId, int permissions) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(SERVER_ID, serverId);
                parameters.putString(USER_ID, userId);
                DatabaseParameters set = new DatabaseParameters();
                set.putInt(PERMISSIONS, permissions);
                UPDATE(set, parameters);
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    public ArrayList<ServerModel> get(String owner) {
        ArrayList<ServerModel> servers = new ArrayList<>();
        try {
            synchronized (Connection.getServers().lock) {
                synchronized (lock) {
                    PreparedStatement stmt = getConnection().prepareStatement(
                            "SELECT * FROM server INNER JOIN (SELECT server_id, permissions as p FROM user_servers WHERE user_id = ?) as t ON serverId=t.server_id");
                    stmt.setString(1, owner);
                    ResultSet set = stmt.executeQuery();
                    while (set.next()) {
                        ServerModel model = Servers.create(set);
                        model.permissions2 = set.getInt("p");
                        servers.add(model);
                    }
                    stmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

}

