package com.aghajari.call;

import com.aghajari.api.SocketApi;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.SoundModel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SoundSender extends Thread {
    private TargetDataLine microphone;
    private final String userId;

    public SoundSender(String userId) {
        this.userId = userId;
        try {
            AudioFormat af = new AudioFormat(44100.0F, 8, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(af);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        microphone.start();
        while (CallUI.isInCall()) {
            SoundModel soundModel = new SoundModel(userId);
            soundModel.bytesRead = microphone.read(soundModel.bytes, 0, soundModel.bytes.length);
            if (soundModel.bytesRead > 0)
                SocketApi.getInstance().write(
                        new SocketModel(SocketEvents.CALL_AUDIO, soundModel)
                );
            else if (soundModel.bytesRead == -1)
                break;
        }
        microphone.close();
    }
}
