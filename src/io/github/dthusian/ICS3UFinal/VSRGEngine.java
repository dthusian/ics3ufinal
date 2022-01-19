package io.github.dthusian.ICS3UFinal;

import java.io.FileReader;
import java.io.IOException; //ioexception from osu how
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VSRGEngine {
    public long startTime = 0;
    public Song currentSong;
    
    public VSRGEngine(Song song) throws IOException, RuntimeException {
        currentSong = song;
        Timer mapTimer = new Timer();
        
        /* have each note as an individual timer that does something when its time
        for it to be drawn on screen/hit
        */
        for (int i = 0; i < currentSong.notes.size(); i++) { // idk how this will work with large number of notes
            mapTimer.schedule(currentSong.notes.get(i).timer, currentSong.notes.get(i).time);
        }
    }
}
