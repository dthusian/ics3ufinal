package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VSRGAudio {
    // Represents a currently running audio stream
    // Most of the code is in loadMusic2 and this is just for defining some methods
    // to communicate with the thread
    static class AudioStream {
        public Thread thread;
        public AtomicInteger signal; // 0 = play, 1 = pause, 2 = stop
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

    static class SoundEffect {
        public AudioFormat format;
        public byte[] data;
        public SoundEffect(String path) throws UnsupportedAudioFileException, IOException {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            data = ais.readAllBytes();
            format = ais.getFormat();
            ais.close();
        }
    }

    static HashMap<String, SoundEffect> sfxs = new HashMap<>();
    static Lock sfxLock = new ReentrantLock();
    static Queue<Clip> clipQueue = new ArrayDeque<>();
    static Thread sfxThread = new Thread(() -> {
        while(true) {
            sfxLock.lock();
            Clip clip;
            if(!clipQueue.isEmpty()) {
                clip = clipQueue.remove();
            } else {
                clip = null;
            }
            sfxLock.unlock();
            if(clip != null) {
                clip.addLineListener(event -> {
                    if(event.getType() == LineEvent.Type.STOP){
                        event.getLine().close();
                    }
                });
                clip.start();
            }
        }
    });

    static {
        sfxThread.start();
    }

    // Play a single sound effect
    public static void playSfx(String path) throws RuntimeException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        // gets the sound effect from cache
        if(!sfxs.containsKey(path)) {
            sfxs.put(path, new SoundEffect(path));
        }
        Clip clip = AudioSystem.getClip();
        clip.open(sfxs.get(path).format, sfxs.get(path).data, 0, sfxs.get(path).data.length);
        sfxLock.lock();
        clipQueue.add(clip);
        sfxLock.unlock();
    }

    // this is the most ungodly function
    // I really hate SourceDataLine
    public static AudioStream loadMusic2(String path, double startAtSeconds) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File file = new File(path);
        // Load audio input stream
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
        AudioFormat audioFormat = ais.getFormat();
        // Setup data line
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();
        ais.skip((int) Math.floor(audioFormat.getFrameSize() * audioFormat.getFrameRate() * startAtSeconds));
        // Setup thread
        AtomicInteger signal = new AtomicInteger(1);
        AtomicInteger signalData = new AtomicInteger(0);
        Thread audioThread = new Thread(() -> {
            try {
                byte[] samples = new byte[4096];
                int count = 0;
                while (true) {
                    // Check the signal for things to do
                    int signalToken = signal.get();
                    if (signalToken != 0) {
                        if (signalToken == 1) {
                            while (signalToken == 1) {
                                // When paused, check every 10ms for unpause
                                Thread.sleep(10);
                                signalToken = signal.get();
                            }
                        } else if (signalToken == 2) {
                            break;
                        }
                    }
                    // Normal operation: read samples and write them
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
        // calculate the duration of the file
        // 99% accurate
        long audioFileLength = file.length();
        int frameSize = audioFormat.getFrameSize();
        double frameRate = audioFormat.getFrameRate();
        double durationInSeconds = (audioFileLength / (frameSize * frameRate));
        return new AudioStream(audioThread, signal, durationInSeconds);
    }

    // Overload for default starting at beginning
    public static AudioStream loadMusic2(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return loadMusic2(path, 0);
    }
}
