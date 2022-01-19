package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class VSRGAudio {
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
    public Clip loadMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(new File(path)));
        return clip;
    }
}
