package io.github.dthusian.ICS3UFinal;

import java.io.IOException;
import java.util.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class VSRGEngine {
    // Time to get ready
    public static final long GRACE_TIME = 2000;
    // Time from note appearing to click (might appear offscreen)
    public static final long APPROACH_TIME = 1340;
    // Time from note passing the scanline to not being rendered
    public static final long MISS_TIME = 160;
    
    // Early hit window
    public static final long DISPATCH_TIME = 160;
    // Late hit window uses MISS_TIME

    // System.currentTimeMillis() when map started (when masterTime = 0)
    public long startTime;
    // This is the time since last tick
    public long masterTime;

    // Index of last note that was retired (went offscreen)
    public int retireNoteI = 0;
    // Index of last note that was dispatched (came onscreen)
    public int dispatchNoteI = 0;
    public Song currentSong;

    public long tickCount = 0;
    
    public boolean[] clickedNotes;
    public boolean[] keysPressed = new boolean[4];

    public VSRGEngine(Song song) throws RuntimeException {
        currentSong = song;
        clickedNotes = new boolean[currentSong.notes.size()];
        startTime = System.currentTimeMillis() + GRACE_TIME;
        masterTime = -GRACE_TIME;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                song.audio.resume();
                System.out.println("Audio Started");
            }
        }, GRACE_TIME);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
            	/*
                System.out.println("TPS: " + tickCount * 4);
                System.out.println("Time: " + masterTime);
                
                System.out.println("Dispatched Notes: " + dispatchNoteI);
                System.out.println("Retired Notes: " + retireNoteI);
                */
                tickCount = 0;
            }
        }, 250, 250);
    }

    public void tick() {
        tickCount++;
        long newTime = System.currentTimeMillis() - startTime;
        for (int i = retireNoteI + 1; i < currentSong.notes.size(); i++) {
            Note currentNote = currentSong.notes.get(i);
            if (Util.between(masterTime, currentNote.time - APPROACH_TIME, newTime)) {
                //currentNote.posY = (int) (((time - currentNote.time) / 2) - 60);
                dispatchNoteI = Math.max(dispatchNoteI, i);
            }
            if (Util.between(masterTime, currentNote.time + MISS_TIME, newTime)) {
                retireNoteI = Math.max(retireNoteI, i);
            }
            if (Util.between(masterTime, currentNote.time - APPROACH_TIME * 2, newTime)) {
                break;
            }
        }
        masterTime = newTime;
    }
    
    public void press(int lane) {
    	long currentTime = System.currentTimeMillis() - startTime;
    	
    	// might be changed for long notes
    	if (keysPressed[lane]) return;
    	keysPressed[lane] = true;
    	
    	for (int i = retireNoteI; i < dispatchNoteI + 1; i++) {
    		Note currentNote = currentSong.notes.get(i);
    		if (currentNote.lane == lane && Util.between(currentNote.time - DISPATCH_TIME, currentTime, currentNote.time + MISS_TIME) && !clickedNotes[i]) {
				System.out.println("Note " + i + " hit at " + currentTime + " (" + currentNote.time + ") on lane " + lane);
				clickedNotes[i] = true;
				
				// hitsound test
				try {
					VSRGAudio.playSfx(VSRGAudio.loadSfx("run/normal-hitwhistle.wav"));
				} catch (RuntimeException | LineUnavailableException | UnsupportedAudioFileException | IOException e1) {
					e1.printStackTrace();
				}
				
				break;
    		}
    	}
    }
    
    public void release(int lane) {
    	long currentTime = System.currentTimeMillis() - startTime;
    	
    	if (!keysPressed[lane]) return;
    	keysPressed[lane] = false;
    }
}
