package io.github.dthusian.ICS3UFinal;

import java.util.*;

public class VSRGEngine {
    // Time from click of button to start of note stream
    public static final long PREMAP_TIME = 2000;
    // Time from note appearing to click (might appear offscreen)
    public static final long APPROACH_TIME = 1340;
    // Time from
    public static final long MISS_TIME = 160;

    // System.currentTimeMillis() when map started
    public long startTime = 0;
    // -2000 -> 0: padding time before start
    // 0 -> audioLeadIn: map started but audio not
    // audioLeadIn -> inf: map and audio up
    // This is the time since last tick
    public long masterTime = -PREMAP_TIME;

    // Index of last note that was retired (went offscreen)
    public int retireNoteI = 0;
    // Index of last note that was dispatched (came onscreen)
    public int dispatchNoteI = 0;
    public Song currentSong;

    public long tickCount = 0;

    public VSRGEngine(Song song) throws RuntimeException {
        currentSong = song;
        startTime = System.currentTimeMillis() - PREMAP_TIME;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                song.audio.resume();
            }
        }, song.audioLeadInMs);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TPS: " + tickCount);
                tickCount = 0;
            }
        }, 1000, 1000);
    }

    public void tick() {
        tickCount++;
        long time = System.currentTimeMillis() - startTime;
        for (int i = retireNoteI + 1; i < currentSong.notes.size(); i++) {
            Note currentNote = currentSong.notes.get(i);
            if (time > currentNote.time - APPROACH_TIME) {
                currentNote.posY = (int) (((time - currentNote.time) / 2) - 60);
                dispatchNoteI = i;
            }
            if (time > currentNote.time + MISS_TIME) {
                retireNoteI = i;
            }
            if (time < currentNote.time - APPROACH_TIME * 2) {
                break;
            }
        }
        masterTime = time;
    }
}
