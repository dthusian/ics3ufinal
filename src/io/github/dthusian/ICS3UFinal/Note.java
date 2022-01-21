package io.github.dthusian.ICS3UFinal;

import java.util.Timer;
import java.util.TimerTask;

public class Note {
    public int time = 0;
    public int lane = 0;
    public int type = 0; // long note/normal note
    public int posX = 0; // probably needed for drawing them
    public int posY = 0;
    public int endTime = 0; // only applies to long note
    NoteTimer timer;
  
    public Note(int time, int lane, int type, int endTime) {
        this.time = time;
        this.lane = lane;
        this.endTime = endTime;
        this.type = type;
      
        this.timer = new NoteTimer(this.time);
    }
}

// so that the timer thing works
class NoteTimer extends TimerTask {
    public int time = 0;
  
    public NoteTimer(int time) {
        this.time = time;
    }
  
    public void run() {
		System.out.println(time);
    }
}
