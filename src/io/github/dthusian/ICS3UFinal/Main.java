package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
    	VSRGAudio.AudioStream as = VSRGAudio.loadMusic2("run/xnor.wav");
        Thread.sleep(10000);
    }
}
