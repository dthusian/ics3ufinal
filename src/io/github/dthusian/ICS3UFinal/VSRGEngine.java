/*
 * 
 * This class is used for handling timing and scoring.
 * It ensures that the game is not falling behind while
 * still only checking about 60 times a second for
 * performance reasons. It also handles key presses,
 * and determines whether or not the player was late,
 * on time, or early.
 * 
 */

package io.github.dthusian.ICS3UFinal;

import java.io.File;
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
    // Time after end of map
    public static final long END_TIME = 3000;

    // System.currentTimeMillis() when map started (when masterTime = 0)
    public long startTime;
    // This is the time since last tick
    public long masterTime;
    // This is the time of the last object
    public long endTime;

    // Index of last note that was retired (went offscreen)
    public int retireNoteI = 0;
    // Index of last note that was dispatched (came onscreen)
    public int dispatchNoteI = 0;
    public Song currentSong;

    // Score
    public int numPerfect = 0;
    public int numGood = 0;
    public int numBad = 0;
    public int numMiss = 0;
    
    public int lastJudgement = -1; // 0 = miss, 1 = bad, 2 = good, 3 = perfect
    public long lastJudgementTime = -1;
    
    public Timer t = null;

	public VSRGEngine(Song song) throws RuntimeException {
        currentSong = song;
        endTime = 0;
        // reset the state of all notes
        for (int i = 0; i < song.notes.size(); i++) {
            song.notes.get(i).clickState = 0;
            endTime = Math.max(song.notes.get(i).endTime, endTime);
        }
        startTime = System.currentTimeMillis() + GRACE_TIME;
        masterTime = -GRACE_TIME;
        t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                song.audio.resume();
            }
        }, GRACE_TIME);
    }

	public void keyPress(int lane) {
        for(int i = retireNoteI; i < dispatchNoteI + 1; i++) {
            Note currentNote = currentSong.notes.get(i);
            // ignore pressed notes
            if(currentNote.lane != lane) {
                continue; // ignore notes in other lanes
            }
            if(currentNote.clickState != 0) {
                continue; // ignore already clicked notes
            }
            if(!Util.between(masterTime - Util.Timing.msMiss, currentNote.time, masterTime + Util.Timing.msMiss)) {
                continue; // ignore objs outside of hitwindow
            }
            // note is eligible for click
            long msError = Math.abs(masterTime - currentNote.time);
            // figure out judgement and do the appropriate action
            if(msError <= Util.Timing.msPerfect) {
            	this.playHitsound();
                numPerfect++;
                lastJudgement = 3;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msGood) {
            	this.playHitsound();
                numGood++;
                lastJudgement = 2;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msBad) {
            	this.playHitsound();
                numBad++;
                lastJudgement = 1;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msMiss) {
                numMiss++;
                lastJudgement = 0;
                lastJudgementTime = masterTime;
            }
            // long notes go to being held
            if(currentNote.isLongNote()) {
                currentNote.clickState = 2;
            } else {
                currentNote.clickState = 1;
            }
            
            break;
        }
    }

    public void keyRelease(int lane) {
        for(int i = retireNoteI; i < dispatchNoteI + 1; i++) {
            Note currentNote = currentSong.notes.get(i);
            // ignore pressed notes
            if (currentNote.lane != lane) {
                continue; // ignore notes in other lanes
            }
            if (!currentNote.isLongNote()) {
                continue; // ignore non-long notes
            }
            if(currentNote.clickState != 2) {
                continue; // only currently held notes are relevant
            }
            // note release is relevant
            long msError = Math.abs(masterTime - currentNote.endTime);
            if(msError <= Util.Timing.msPerfect) {
            	this.playHitsound();
                numPerfect++;
                lastJudgement = 3;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msGood) {
            	this.playHitsound();
                numGood++;
                lastJudgement = 2;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msBad) {
            	this.playHitsound();
                numBad++;
                lastJudgement = 1;
                lastJudgementTime = masterTime;
            } else if(msError <= Util.Timing.msMiss) {
                numMiss++;
                lastJudgement = 0;
                lastJudgementTime = masterTime;
            } else if(masterTime < currentNote.endTime) {
                // too early :bruh:
            	numMiss++;
            	lastJudgement = 0;
            	lastJudgementTime = masterTime;
                currentNote.clickState = 3;
            }
            
            // change note back to clicked
            if (currentNote.clickState != 3) {
            	currentNote.clickState = 1;
            }
        }
    }

    public void tick() {
        long newTime = System.currentTimeMillis() - startTime;
        for (int i = retireNoteI; i < currentSong.notes.size(); i++) {
            Note currentNote = currentSong.notes.get(i);
            if (Util.between(masterTime, currentNote.time - APPROACH_TIME, newTime)) {
                dispatchNoteI = Math.max(dispatchNoteI, i);
            }
            if (Util.between(masterTime, currentNote.endTime + MISS_TIME, newTime)) {
                // Only retire if the last note was also retired
                if(retireNoteI == i - 1) {
                    retireNoteI = i;
                }
                if (currentNote.clickState == 0) {
                    // Unclicked :bruh:
                    numMiss++;
                    lastJudgement = 0;
                    lastJudgementTime = masterTime;
                }
            }
            if (Util.between(masterTime, currentNote.time - APPROACH_TIME * 2, newTime)) {
                break;
            }
        }
        masterTime = newTime;
        
        // clear judgement message after some time
        if (lastJudgementTime < masterTime - 1500) {
        	lastJudgement = -1;
        }
    }

    public boolean isMapFinished() {
        return masterTime >= endTime + END_TIME;
    }

    public void endMap() {
        currentSong.audio.stop();
        currentSong.audio = null;
    }

    // Calculates game stats based on distribution of perfect/good/bad/miss

    public double accuracy() {
        double acc = 100 * (numBad * 0.25 + numGood * 0.5 + numPerfect) / (numMiss + numBad + numGood + numPerfect);
        if(Double.isNaN(acc)) {
            return 0;
        }
        return acc;
    }

    public int score() {
        return numPerfect * 150 + numGood * 100 + numBad * 50;
    }

    // plays a hitsound
    public void playHitsound() {
    	try {
			VSRGAudio.playSfx("src/io/github/dthusian/ICS3UFinal/hitsounds/normal-hitnormal.wav");
		} catch (LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
            try {
                VSRGAudio.playSfx("hitsounds/normal-hitnormal.wav");
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
