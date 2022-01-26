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
        public double length;

        AudioStream(Thread t, AtomicInteger i, double lengthSeconds) {
            thread = t;
            signal = i;
            length = lengthSeconds;
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

        public double getSecondsLength() {
            return length;
        }

        public int getMillisecondsLength() {
            return (int) Math.floor(length * 1000);
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
        if (clip == null) {
            throw new RuntimeException("Clip not found");
        }
        // set to beginning and start
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    // attempt 2
    public static AudioStream loadMusic2(String path, double startAtSeconds) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File file = new File(path);
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
        AudioFormat audioFormat = ais.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();
        ais.skip((int) Math.floor(audioFormat.getFrameSize() * audioFormat.getFrameRate() * startAtSeconds));
        AtomicInteger signal = new AtomicInteger(1);
        AtomicInteger signalData = new AtomicInteger(0);
        Thread audioThread = new Thread(() -> {
            try {
                byte[] samples = new byte[4096];
                int count = 0;
                while (true) {
                    int signalToken = signal.get();
                    if (signalToken != 0) {
                        if (signalToken == 1) {
                            while (signalToken == 1) {
                                Thread.sleep(1000);
                                signalToken = signal.get();
                            }
                        } else if (signalToken == 2) {
                            break;
                        }
                    }
                    count = ais.read(samples, 0, 4096);
                    if (count == -1) break;
                    line.write(samples, 0, count);
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
        long audioFileLength = file.length();
        int frameSize = audioFormat.getFrameSize();
        double frameRate = audioFormat.getFrameRate();
        double durationInSeconds = (audioFileLength / (frameSize * frameRate));
        AudioStream as = new AudioStream(audioThread, signal, durationInSeconds);
        return as;
    }

    public static AudioStream loadMusic2(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return loadMusic2(path, 0);
    }
}
