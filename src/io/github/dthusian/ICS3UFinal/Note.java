package io.github.dthusian.ICS3UFinal;

import java.util.Timer;
import java.util.TimerTask;

public class Note {
    public int time = 0;
    public int lane = 0;
    public int posX = 0;
    public int posY = 0;
    NoteTimer timer;
  
    public Note(int time, int lane) {
        this.time = time;
        this.lane = lane;
      
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
