package com.aghajari.types;

import com.aghajari.OnlineUsers;
import com.aghajari.UserThread;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.SoundModel;

import java.util.Vector;

public class Terminate {

    private Terminate() {
    }

    public static void run(UserThread thread, SocketModel model) {
        String key = model.get();
        Vector<UserThread> threads = OnlineUsers.getDevices(thread);
        for (UserThread t : threads) {
            if (t != thread && t.getDeviceInfo().key.equals(key)) {
                t.terminate();
                break;
            }
        }
    }

}
