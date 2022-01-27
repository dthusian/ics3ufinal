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
