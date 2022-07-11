package com.aghajari;

import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.DeviceInfo;
import com.aghajari.shared.models.IsTypingModel;
import com.aghajari.shared.models.QRResult;
import com.aghajari.shared.models.UserModel;
import com.aghajari.types.Factory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserThread extends Thread {

    private final Socket socket;
    private String clientId = null;
    private String token = null;
    private DeviceInfo deviceInfo = null;

    private final ObjectOutputStream writer;
    private final ObjectInputStream reader;
    private String checkOnlineForUserId = null;
    private String checkChatForUserId = null;
    private IsTypingModel typingModel = null;
    private String qrKey;

    UserThread(Socket socket) throws IOException {
        this.socket = socket;
        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                SocketModel model = (SocketModel) reader.readObject();

                if (isInterrupted())
                    break;

                if (clientId == null && model.eventType == SocketEvents.QRCODE) {
                    setDeviceInfo(model.get());
                    closeQR();
                    qrKey = "Aghajari-" + Main.rnd();
                    OnlineUsers.addQRListener(qrKey, this);
                    write(new SocketModel(SocketEvents.QRCODE, qrKey));
                    continue;
                }

                // What are you waiting for?
                if (model.eventType != SocketEvents.AUTH && clientId == null)
                    break;

                closeQR();

                if (model.eventType == SocketEvents.IS_TYPING) {
                    typingModel = model.get();
                    OnlineUsers.requestIsTyping(this, typingModel);
                    continue;
                }

                if (model.eventType == SocketEvents.API && "isOnline".equals(model.name))
                    checkOnlineForUserId = (String) ((Object[]) model.get())[0];

                if (model.eventType == SocketEvents.API && "getMessages".equals(model.name)) {
                    Object[] args = model.get();
                    if (args == null || args.length == 0)
                        checkChatForUserId = null; // saved messages
                    else
                        checkChatForUserId = (String) args[0];
                }

                Factory.run(this, model);
            } catch (Exception ignore) {
                break;
            }
        }

        close();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        close();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
        if (socket.isClosed())
            return;
        OnlineUsers.addUser(clientId, this);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean write(SocketModel model) {
        return write(model, false);
    }

    public boolean write(SocketModel model, boolean unshared) {
        try {
            if (unshared) {
                getWriter().writeUnshared(model);
            } else {
                getWriter().writeObject(model);
            }
            return true;
        } catch (IOException ignore) {
        }
        return false;
    }

    public ObjectOutputStream getWriter() {
        return writer;
    }

    public ObjectInputStream getReader() {
        return reader;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getCheckOnlineForUserId() {
        return checkOnlineForUserId;
    }

    public String getCheckChatForUserId() {
        return checkChatForUserId;
    }

    public IsTypingModel getTypingModel() {
        return typingModel;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void terminate(){
        setDeviceInfo(null);
        write(new SocketModel(SocketEvents.TERMINATE, null), true);
        close();
    }

    public void registerByQRCode(UserThread thread, UserModel model) {
        if (socket.isClosed())
            return;

        setToken(thread.getToken());
        setClientId(thread.getClientId());
        closeQR();
        write(new SocketModel(SocketEvents.QRCODE_AUTH, new QRResult(getToken(), model)));
    }

    public void close() {
        try {
            if (typingModel != null && typingModel.isTyping) {
                typingModel.isTyping = false;
                OnlineUsers.requestIsTyping(this, typingModel);
            }
            closeQR();
            OnlineUsers.removeUser(this);
            socket.close();
        } catch (IOException ignore) {
        }
    }

    public String getQRKey() {
        return qrKey;
    }

    public void closeQR() {
        if (qrKey != null) {
            OnlineUsers.removeQR(qrKey);
            qrKey = null;
        }
    }
}