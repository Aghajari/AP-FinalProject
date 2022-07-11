package com.aghajari.call;

import com.aghajari.api.SocketApi;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.models.SoundModel;

import javax.sound.sampled.*;

public class SoundReceiver {
    SourceDataLine inSpeaker;
    SocketApi.Listener listener;

    public SoundReceiver() {
        try {
            AudioFormat af = new AudioFormat(44100.0F, 8, 2, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            inSpeaker = (SourceDataLine) AudioSystem.getLine(info);
            inSpeaker.open(af);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        inSpeaker.start();
        listener = model -> {
            SoundModel sm = model.get();
            inSpeaker.write(sm.bytes, 0, sm.bytesRead);
        };
        SocketApi.getInstance().register(SocketEvents.CALL_AUDIO, listener);
    }

    public void close(){
        inSpeaker.close();
        SocketApi.getInstance().unregister(listener);
    }
}