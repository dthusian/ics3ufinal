/*
 * 
 * This class stores information about each note, which is useful
 * when determining where a note should go and when it should be clicked.
 * 
 */

package io.github.dthusian.ICS3UFinal;

import java.util.Timer;
import java.util.TimerTask;

public class Note {
    public int time = 0;
    public int lane = 0;
    public int type = 0; // long note/normal note
    public int endTime = 0; // only applies to long note (msut be equal to time if not long note)
    public int clickState = 0; // 0 = unclicked, 1 = clicked, 2 = during click (long note), 3 = released too early (long note)

    public Note(int time, int lane, int type, int endTime) {
        this.time = time;
        this.lane = lane;
        this.endTime = endTime;
        this.type = type;
    }

    public boolean isLongNote() {
        return type == 0b10000000;
    }
}