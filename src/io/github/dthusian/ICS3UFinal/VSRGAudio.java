package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class VSRGAudio {

    // Class that represents a currently loaded and playing audio file
    // Doesn't work right now
    static class AudioStream implements Runnable {
        SourceDataLine dataLine;
        Thread audioThread;
        AudioInputStream input;

        public AudioStream(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            input = AudioSystem.getAudioInputStream(new File(path));
            AudioFormat fmt = input.getFormat();
            double bytesPerS = (double)fmt.getFrameSize() * (double)fmt.getFrameRate();
            double bytesPerInterval = bytesPerS * 50 / 1000;
            int roundedBufSize = (int)Math.round(bytesPerInterval / (double)fmt.getFrameSize() * fmt.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt, roundedBufSize);
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            audioThread = new Thread(this);
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

        @Override
        public void run() {
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

    // internal cache of sound effects
    HashMap<String, Clip> sfxs;

    public VSRGAudio() {
        sfxs = new HashMap<>();
    }

    // Class that loads a sound effect to play at a later time
    // The string returned from this should be inputted into playSfx

    public String loadSfx(String path) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        // creates a semi-unique key based on the filename
        String key = Paths.get(path).getFileName().toString();
        // open clip and put it into cache
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        sfxs.put(key, clip);
        return key;
    }

    public void playSfx(String key) throws RuntimeException {
        // gets the sound effect from cache
        Clip clip = sfxs.get(key);
        if(clip == null) {
            throw new RuntimeException("Clip not found");
        }
        // set to beginning and start
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    // loads a longer audio file for playing
    // note that shorter files should be played with the sfx methods
    public AudioStream loadMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return new AudioStream(path);
    }
}
