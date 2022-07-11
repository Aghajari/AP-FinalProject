package com.aghajari.types;

import com.aghajari.UserThread;
import com.aghajari.api.SocketApi;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;

import java.lang.reflect.Method;

public class Api {

    private Api() {
    }

    public static void run(UserThread thread, SocketModel model) {
        int size = model.data == null ? 0 : ((Object[]) model.data).length;
        Object[] args = new Object[size + 1];
        args[0] = thread;
        if (size != 0)
            System.arraycopy((Object[]) model.data, 0, args, 1, size);

        Class<?>[] parameters = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            if (args[i] == null)
                parameters[i] = Object.class;
            else
                parameters[i] = args[i].getClass();

        try {
            Method method = SocketApi.class.getMethod(model.name, parameters);
            Object out = method.invoke(null, args);
            thread.write(new SocketModel(SocketEvents.API, model.name, out));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

}
