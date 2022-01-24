package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class VSRGAudio {
    static class AudioStream {
        public Thread thread;
        public AtomicInteger signal;

        AudioStream(Thread t, AtomicInteger i) {
            thread = t;
            signal = i;
        }

        public void pause() {
            signal.set(1);
        }

        public void resume() {
            signal.set(0);
        }

        public void stop() {
            signal.set(2);
        }
    }

    // internal cache of sound effects
    static HashMap<String, Clip> sfxs = new HashMap<>();

    // Class that loads a sound effect to play at a later time
    // The string returned from this should be inputted into playSfx

    public static String loadSfx(String path) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        // creates a semi-unique key based on the filename
        String key = Paths.get(path).getFileName().toString();
        // open clip and put it into cache
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        sfxs.put(key, clip);
        return key;
    }

    public static void playSfx(String key) throws RuntimeException {
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
    public static Clip loadMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        return clip;
    }

    // attempt 2
    public static AudioStream loadMusic2(String path) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(path))));
        AudioFormat audioFormat = ais.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();
        AtomicInteger signal = new AtomicInteger(0);
        Thread audioThread = new Thread(() -> {
            try {
                byte[] samples = new byte[4096];
                int count = 0;
                while ((count = ais.read(samples, 0, 4096)) != -1) {
                    line.write(samples, 0, count);
                    int signalToken = signal.get();
                    if(signalToken != 0) {
                        if(signalToken == 1) {
                            while(signalToken == 1) {
                                Thread.sleep(1000);
                                signalToken = signal.get();
                            }
                        } else if(signalToken == 2) {
                            break;
                        }
                    }
                }
                line.drain();
                line.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        audioThread.start();
        AudioStream as = new AudioStream(audioThread, signal);
        return as;
    }
}
