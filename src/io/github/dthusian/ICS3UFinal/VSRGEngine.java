package io.github.dthusian.ICS3UFinal;

import java.util.*;

public class VSRGEngine {
    // Time to get ready
    public static final long GRACE_TIME = 2000;
    // Time from note appearing to click (might appear offscreen)
    public static final long APPROACH_TIME = 1340;
    // Time from note passing the scanline to not being rendered
    public static final long MISS_TIME = 160;

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

    public VSRGEngine(Song song) throws RuntimeException {
        currentSong = song;
        startTime = System.currentTimeMillis() + GRACE_TIME + song.audioLeadInMs;
        masterTime = -(GRACE_TIME + song.audioLeadInMs);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                song.audio.resume();
            }
        }, GRACE_TIME);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TPS: " + tickCount * 10);
                System.out.println("Time: " + masterTime);
                System.out.println("Retired Notes: " + retireNoteI);
                System.out.println("Dispatched Notes: " + dispatchNoteI);
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
}
