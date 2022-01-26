package io.github.dthusian.ICS3UFinal;

import java.util.Timer;
import java.util.TimerTask;

public class Note {
    public int time = 0;
    public int lane = 0;
    public int type = 0; // long note/normal note
    public int endTime = 0; // only applies to long note

    public Note(int time, int lane, int type, int endTime) {
        this.time = time;
        this.lane = lane;
        this.endTime = endTime;
        this.type = type;
    }
}