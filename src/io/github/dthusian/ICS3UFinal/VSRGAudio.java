package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class VSRGAudio {
    static class AudioStream {
        SourceDataLine dataLine;
        AudioInputStream input;
        Thread audioThread;
        boolean paused;

        public AudioStream(String path, Mixer mixer) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            this.input = AudioSystem.getAudioInputStream(new File(path));
            AudioFormat fmt = this.input.getFormat();
            double bytesPerS = (double)fmt.getFrameSize() * (double)fmt.getFrameRate();
            this.dataLine = (SourceDataLine) mixer.getLine(new DataLine.Info(SourceDataLine.class, fmt, (int)(bytesPerS * 50 / 1000)));
            this.audioThread = new Thread(() -> threadMain());
            paused = false;
        }

        public void start() {
            this.audioThread.start();
        }

        public double getSecondPosition() {
            return this.dataLine.getMicrosecondPosition() / 1000000.0;
        }

        public void pause() {
            synchronized (this) {
                paused = true;
            }
        }

        public void resume() {
            synchronized (this) {
                paused = false;
            }
        }

        private void threadMain() {
            try {
                while(true) {
                    byte[] buf = new byte[dataLine.getBufferSize()];
                    int read = input.read(buf);
                    dataLine.write(buf, 0, buf.length);
                    if(read == 0) break;
                    synchronized (this) {
                        if(paused) {
                            Thread.sleep(5);
                        }
                    }
                }
            } catch(IOException | InterruptedException ignored) {
            }
        }
    }

    Mixer mixer;
    HashMap<String, Clip> sfxs;

    public VSRGAudio() {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        System.out.println("Discovering Audio Devices...");
        for(int i = 0; i < mixerInfos.length; i++) {
            Mixer testMixer = AudioSystem.getMixer(mixerInfos[i]);
            if (testMixer.getMixerInfo().getName().startsWith("default")) {
                mixer = AudioSystem.getMixer(mixerInfos[0]);
                break;
            }
        }
        if(mixer == null) {
            throw new RuntimeException("No default audio device found");
        }
        sfxs = new HashMap<>();
    }

    public String loadSfx(String path) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String key = Paths.get(path).getFileName().toString();
        Clip clip = (Clip) mixer.getLine(new Line.Info(Clip.class));
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        sfxs.put(key, clip);
        return key;
    }

    public void playSfx(String key) throws RuntimeException {
        Clip clip = sfxs.get(key);
        if(clip == null) {
            throw new RuntimeException("Clip not found");
        }
        clip.start();
    }

    public AudioStream loadMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return new AudioStream(path, mixer);
    }
}
