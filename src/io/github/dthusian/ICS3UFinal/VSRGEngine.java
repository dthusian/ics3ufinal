package io.github.dthusian.ICS3UFinal;

import java.io.FileReader;
import java.io.IOException; //ioexception from osu how
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VSRGEngine {
  public static final long PREMAP_TIME = 2000;
  public static final long APPROACH_TIME = 1340;
  public static final long MISS_TIME = 160;

  // System.currentTimeMillis() when map started
  public long startTime = 0;
  // -2000 -> 0: padding time before start
  // 0 -> audioLeadIn: map started but audio not
  // audioLeadIn -> inf: map and audio up
  // This is the time since last tick
  public long masterTime = -PREMAP_TIME;
  public int lastNoteI = 0;
  public Song currentSong;

  public VSRGEngine(Song song) throws RuntimeException {
    currentSong = song;
    startTime = System.currentTimeMillis() - PREMAP_TIME;
    (new Timer()).schedule(new TimerTask() {
      @Override
      public void run() {
        song.audio.resume();
      }
    }, song.audioLeadInMs);
  }

  public void tick() {
    long time = System.currentTimeMillis() - startTime;
    for (int i = lastNoteI + 1; i < currentSong.notes.size(); i++) {
      Note currentNote = currentSong.notes.get(i);
      if (time > currentNote.time - APPROACH_TIME) {
        System.out.println(i);
        currentNote.drawn = true;
        currentNote.posY = (int) (((time - currentSong.notes.get(i).time) / 2) - 60);
        lastNoteI = i;
      }
      if (time > currentSong.notes.get(i).time + MISS_TIME) {
        currentNote.drawn = false;
      }
      if (time < currentNote.time - APPROACH_TIME * 2) {
        break;
      }
      //if (currentNote.drawn)
        //System.out.printf("%d: %d %d%n", time, currentSong.notes.get(i).posX, currentSong.notes.get(i).posY);
    }
    masterTime = time;
  }
}
