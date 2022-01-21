package io.github.dthusian.ICS3UFinal;

import java.io.FileReader;
import java.io.IOException; //ioexception from osu how
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VSRGEngine {
    public long startTime = 0;
    public long time = -1600;
    public Song currentSong;
    
    public VSRGEngine(Song song) throws IOException, RuntimeException {
        currentSong = song;
        Timer mapTimer = new Timer();
        
        /* have each note as an individual timer that does something when its time
        for it to be drawn on screen/hit
        */
        /*for (int i = 0; i < currentSong.notes.size(); i++) { // idk how this will work with large number of notes
            mapTimer.schedule(currentSong.notes.get(i).timer, currentSong.notes.get(i).time);
        }*/
        startTime = System.currentTimeMillis();
    }
    
    public void tick() {
    	time = System.currentTimeMillis() - startTime;
    	for (int i = 0; i < currentSong.notes.size(); i++) {
    		if (time > currentSong.notes.get(i).time - 1340) {
    			currentSong.notes.get(i).drawn = true;
    			currentSong.notes.get(i).posY = (int)(((time - currentSong.notes.get(i).time) / 2) - 60);
    		}
    		if (time > currentSong.notes.get(i).time + 160) {
    			currentSong.notes.get(i).drawn = false;
    		}
    		if (time < currentSong.notes.get(i).time - 2000) {
    			break;
    		}
    		if (currentSong.notes.get(i).drawn) System.out.printf("%d: %d %d%n", time, currentSong.notes.get(i).posX, currentSong.notes.get(i).posY);
    	}
    }
}
