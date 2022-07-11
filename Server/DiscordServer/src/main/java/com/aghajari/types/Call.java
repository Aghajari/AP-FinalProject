package com.aghajari.types;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.SoundModel;

public class Call {

    private Call() {
    }

    public static void run(UserThread thread, SocketModel model) {
        SoundModel sm = model.get();
        String id = sm.userId;
        sm.userId = thread.getClientId();
        OnlineUsers.send(id, new SocketModel(SocketEvents.CALL_AUDIO, sm));
    }

}
