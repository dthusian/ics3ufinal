/*
 * 
 * Names: Brendan Tam, Adrian Wu
 * Date: January 27, 2022
 * 
 * Description: 
 * This is a game inspired by classic mania-type VSRGs.
 * Each lane is assigned a key (DFJK and lanes 1, 2, 3, 4 respectively) and the player
 * must tap that key when the note reaches the line. If the note is a long note, the
 * player must hold until the long note is complete, and release on time. Scoring is
 * entirely accuracy based, as this is a rhythm game. The closer you are to hitting
 * on time, the more score you get. At the end of the song, a letter grade is assigned
 * to you, showing how well you did. "A" is the best, and "F" is the worst.
 * 
 */

package io.github.dthusian.ICS3UFinal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // New window and add main menu
        JFrame frame = new JFrame("CS Mania");
        frame.add(new Menu(frame));
        frame.pack();
        frame.setVisible(true);
    }
}
