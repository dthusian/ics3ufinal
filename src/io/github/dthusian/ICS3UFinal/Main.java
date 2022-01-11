package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        VSRGAudio audioEng = new VSRGAudio();
        VSRGAudio.AudioStream stream = audioEng.loadMusic("xnor.wav");
        stream.start();
        Thread.sleep(10000);
        stream.pause();
        Thread.sleep(10000);
        stream.resume();
    }
}
