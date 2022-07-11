package com.aghajari.types;

import com.aghajari.UserThread;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;

public class Factory {

    private Factory() {
    }

    public static void run(UserThread thread, SocketModel model) {
        switch (model.eventType) {
            case SocketEvents.AUTH -> Auth.run(thread, model);
            case SocketEvents.API -> Api.run(thread, model);
            case SocketEvents.REQUEST_CALL,
                    SocketEvents.ANSWER_CALL,
                    SocketEvents.REJECT_CALL,
                    SocketEvents.ALREADY_IN_CALL -> RequestCall.run(thread, model);
            case SocketEvents.CALL_AUDIO -> Call.run(thread, model);
            case SocketEvents.FRIENDSHIP_REQUEST -> Friendship.run(thread, model);
            case SocketEvents.TERMINATE -> Terminate.run(thread, model);
        }
    }

}
