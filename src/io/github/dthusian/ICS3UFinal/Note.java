package io.github.dthusian.ICS3UFinal;

import java.util.Timer;
import java.util.TimerTask;

public class Note {
    public int time = 0;
    public int lane = 0;
    public int type = 0;
    public int posX = 0;
    public int posY = 0;
    public int endTime = 0;
    NoteTimer timer;
  
    public Note(int time, int lane, int type, int endTime) {
        this.time = time;
        this.lane = lane;
	this.endTime = endTime;
	this.type = type;
      
        this.timer = new NoteTimer(this.time);
    }
}

class NoteTimer extends TimerTask {
    public int time = 0;
  
    public NoteTimer(int time) {
        this.time = time;
    }
  
    public void run() {
		    
    }
}
