package com.aghajari.types;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.database.Connection;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;

public class Friendship {

    private Friendship() {
    }

    public static void run(UserThread thread, SocketModel model) {
        try {
            synchronized (Connection.getFriendship()) {
                FriendshipRequestModel m = model.get();
                switch (m.type) {
                    case FriendshipRequestModel.SEND_REQUEST -> {
                        FriendshipModel m2 = Connection.getFriendship().select(thread.getClientId(), m.id);
                        if (m2.exists()) {
                            if (m2.accepted || m2.toId.equals(thread.getClientId())) {
                                Connection.getFriendship().update(true, m2.index);
                                OnlineUsers.send(thread.getClientId(),
                                        new SocketModel(SocketEvents.FRIENDSHIP_NOTIFY, m2));
                            }
                        } else {
                            Connection.getFriendship().insert(thread.getClientId(), m.id);
                            OnlineUsers.sendFriendship(thread, m.id,
                                    new SocketModel(SocketEvents.FRIENDSHIP_NOTIFY,
                                            Connection.getFriendship().select(thread.getClientId(), m.id)));
                        }
                    }
                    case FriendshipRequestModel.ACCEPT_REQUEST -> {
                        Connection.getFriendship().update(true, m.keyIndex);
                        OnlineUsers.sendFriendship(thread, m.id,
                                new SocketModel(SocketEvents.FRIENDSHIP_NOTIFY,
                                        Connection.getFriendship().select(m.keyIndex)));
                    }
                    case FriendshipRequestModel.CANCEL_FRIENDSHIP -> {
                        if (m.keyIndex == -1) {
                            Connection.getFriendship().delete(thread.getClientId(), m.id);
                        } else {
                            Connection.getFriendship().update(false, m.keyIndex);
                        }
                        OnlineUsers.sendFriendship(thread, m.id,
                                new SocketModel(SocketEvents.FRIENDSHIP_NOTIFY,
                                        new FriendshipModel(-1, m.id, thread.getClientId(), false)));
                    }
                    case FriendshipRequestModel.NOTIFY_FRIENDSHIP ->
                            OnlineUsers.send(thread.getClientId(),
                            new SocketModel(SocketEvents.FRIENDSHIP_NOTIFY,
                                    Connection.getFriendship().select(thread.getClientId(), m.id)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
