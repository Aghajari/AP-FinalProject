package com.aghajari.database.tables;

import com.aghajari.database.DatabaseParameters;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.IDModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Friendship extends AbstractTable {

    private static final String INDEX = "friendships_index";
    private static final String FROM_ID = "fromId";
    private static final String TO_ID = "toId";
    private static final String STATUS = "status";

    public Friendship() {
        super("friendships");
    }

    public void insert(String fromId, String toId) {
        try {
            synchronized (lock) {
                delete(fromId, toId);

                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(FROM_ID, fromId);
                parameters.putString(TO_ID, toId);
                parameters.putBoolean(STATUS, false);
                INSERT(parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(String id, String id2) {
        try {
            synchronized (lock) {
                DatabaseParameters parameters = new DatabaseParameters();
                parameters.putString(FROM_ID, id);
                parameters.putString(TO_ID, id);
                parameters.putString(FROM_ID + 2, id2);
                parameters.putString(TO_ID + 2, id2);

                DELETE(parameters,
                        "(" + FROM_ID + "=? OR " + TO_ID + "=?) AND " +
                                "(" + FROM_ID + "=? OR " + TO_ID + "=?)" + " LIMIT 1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(boolean isAccepted, int friendships_index) {
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putInt(INDEX, friendships_index);

                if (isAccepted) {
                    DatabaseParameters parameters = new DatabaseParameters();
                    parameters.putBoolean(STATUS, true);

                    UPDATE(parameters, whereParameters);
                } else {
                    DELETE(whereParameters);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FriendshipModel select(String id, String id2) {
        AtomicReference<FriendshipModel> friendshipModel = new AtomicReference<>(null);
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, id);
                whereParameters.putString(TO_ID, id);
                whereParameters.putString(FROM_ID + 2, id2);
                whereParameters.putString(TO_ID + 2, id2);

                SELECT("*", whereParameters,
                        "(" + FROM_ID + "=? OR " + TO_ID + "=?) AND " +
                                "(" + FROM_ID + "=? OR " + TO_ID + "=?)" + " LIMIT 1",
                        friends -> {
                            if (friends.next())
                                friendshipModel.set(create(friends));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (friendshipModel.get() == null)
            return new FriendshipModel(-1, id, id2, false);

        return friendshipModel.get();
    }

    public FriendshipModel select(int index) {
        AtomicReference<FriendshipModel> friendshipModel = new AtomicReference<>(null);
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putInt(INDEX, index);

                SELECT("*", whereParameters,
                        friends -> {
                            if (friends.next())
                                friendshipModel.set(create(friends));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (friendshipModel.get() == null)
            return new FriendshipModel(-1, "", "", false);

        return friendshipModel.get();
    }

    public ArrayList<FriendshipModel> get(String id) {
        ArrayList<FriendshipModel> friendsList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, id);
                whereParameters.putString(TO_ID, id);

                SELECT("*", whereParameters,
                        "(" + FROM_ID + "=? OR " + TO_ID + "=?) ORDER BY " + INDEX + " DESC",
                        friends -> {
                            while (friends.next())
                                friendsList.add(create(friends));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendsList;
    }

    public ArrayList<FriendshipModel> get(String id, boolean accepted) {
        ArrayList<FriendshipModel> friendsList = new ArrayList<>();
        try {
            synchronized (lock) {
                DatabaseParameters whereParameters = new DatabaseParameters();
                whereParameters.putString(FROM_ID, id);
                whereParameters.putString(TO_ID, id);
                whereParameters.putBoolean(STATUS, accepted);

                SELECT("*", whereParameters,
                        "(" + FROM_ID + "=? OR " + TO_ID + "=?) AND " + STATUS + "=? ORDER BY " + INDEX + " DESC",
                        friends -> {
                            while (friends.next())
                                friendsList.add(create(friends));
                        });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendsList;
    }

    public ArrayList<IDModel> mutual(String id1, String id2) {
        ArrayList<IDModel> list = new ArrayList<>();
        ArrayList<FriendshipModel> l1 = get(id1, true);
        ArrayList<FriendshipModel> l2 = get(id2, true);
        for (FriendshipModel m1 : l1) {
            String mid = m1.fromId.equals(id1) ? m1.toId : m1.fromId;
            if (mid.equals(id2))
                continue;

            for (FriendshipModel m2 : l2) {
                if (m2.fromId.equals(mid) || m2.toId.equals(mid)) {
                    list.add(new IDModel(mid));
                    break;
                }
            }
        }
        return list;
    }

    private static FriendshipModel create(ResultSet set) throws SQLException {
        return new FriendshipModel(set.getInt(INDEX),
                set.getString(FROM_ID),
                set.getString(TO_ID),
                set.getBoolean(STATUS));
    }
}
