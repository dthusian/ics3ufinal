package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class VSRGAudio {
    static class AudioStream {
        SourceDataLine dataLine;
        Thread audioThread;

        public AudioStream(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            AudioInputStream input = AudioSystem.getAudioInputStream(new File(path));
            AudioFormat fmt = input.getFormat();
            double bytesPerS = (double)fmt.getFrameSize() * (double)fmt.getFrameRate();
            double bytesPerInterval = bytesPerS * 50 / 1000;
            int roundedBufSize = (int)Math.round(bytesPerInterval / (double)fmt.getFrameSize() * fmt.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt, roundedBufSize);
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            audioThread = new Thread(() -> threadMain(input));
        }

        public void start() {
            dataLine.start();
            audioThread.start();
        }

        public double getSecondPosition() {
            return this.dataLine.getMicrosecondPosition() / 1000000.0;
        }

        public void pause() {
            dataLine.stop();
        }

        public void resume() {
            dataLine.start();
        }

        private void threadMain(AudioInputStream input) {
            try {
                while(true) {
                    byte[] buf = new byte[dataLine.getBufferSize()];
                    int read = input.read(buf);
                    dataLine.write(buf, 0, buf.length);
                    if(read == 0) break;
                }
            } catch(IOException ignored) {
            }
        }
    }

    HashMap<String, Clip> sfxs;

    public VSRGAudio() {
        sfxs = new HashMap<>();
    }

    public String loadSfx(String path) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String key = Paths.get(path).getFileName().toString();
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        sfxs.put(key, clip);
        return key;
    }

    public void playSfx(String key) throws RuntimeException {
        Clip clip = sfxs.get(key);
        if(clip == null) {
            throw new RuntimeException("Clip not found");
        }
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    public AudioStream loadMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return new AudioStream(path);
    }
}
