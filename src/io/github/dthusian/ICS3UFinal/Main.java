package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        JFrame frame = new JFrame("CS Mania");
        frame.add(new Menu());
        frame.pack();
        frame.setVisible(true);
        VSRGAudio audioEng = new VSRGAudio();
        VSRGAudio.AudioStream stream = audioEng.loadMusic("xnor.wav");
        stream.start();
        Thread.sleep(10000);
    }
}
