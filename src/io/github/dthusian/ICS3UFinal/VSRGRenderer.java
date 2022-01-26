package io.github.dthusian.ICS3UFinal;

import javax.swing.*;
import java.awt.*;

public class VSRGRenderer {
    VSRGEngine eng;
    boolean[] keysPressed;

    public VSRGRenderer(VSRGEngine eng) {
        this.eng = eng;
        keysPressed = new boolean[4];
    }

    public void keyPress(int lane) {
        keysPressed[lane] = true;
    }

    public void keyRelease(int lane) {
        keysPressed[lane] = false;
    }

    public void drawLane(JPanel panel, Graphics2D g, int xBase, int laneId, Color col) {
        g.setColor(col);
        if (this.keysPressed[laneId]) {
            g.setColor(col);
        } else {
            g.setColor(new Color(70, 70, 70));
        }
        g.fillRect(panel.getWidth() / 2 + xBase, panel.getHeight() - 80, 75, 80);
        g.setColor(new Color(150, 150, 150));
        g.drawRect(panel.getWidth() / 2 + xBase, 0, 75, panel.getHeight());
    }

    public void draw(JPanel panel, Graphics g) {
        // Draw background
        g.drawImage(eng.currentSong.background, 0, 0, null);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(panel.getWidth() / 2 - 150, 0, 300, panel.getHeight() - 80);

        // Draw lanes
        Graphics2D g2d = (Graphics2D)g;
        drawLane(panel, g2d, -150, 0, new Color(250, 180, 240));
        drawLane(panel, g2d, -75, 1, new Color(120, 200, 230));
        drawLane(panel, g2d, 0, 2, new Color(255, 230, 130));
        drawLane(panel, g2d, +75, 3, new Color(160, 230, 150));

        // Score display
        g.setFont(new Font("sans-serif", Font.PLAIN, 52));
        g.drawString("0", 20, 50);
        g.setFont(new Font("sans-serif", Font.PLAIN, 50));
        g.setColor(new Color(255, 255, 255));
        g.drawString("0", 20, 50);

        // Draw notes
        long time = System.currentTimeMillis() - eng.startTime;
        for(int i = eng.retireNoteI; i <= eng.dispatchNoteI; i++) {
            Note currentNote = eng.currentSong.notes.get(i);
            final int NOTE_THICKNESS = 30;
            int posY = (int) ((panel.getHeight() - 80 /* hitline position */) - (currentNote.time - time /* time to line */) * (1.7 /* approach rate */));
            g.fillRect(panel.getWidth() / 2 + -150 + currentNote.lane * 75, posY - NOTE_THICKNESS / 2, 75, NOTE_THICKNESS);
        }
    }
}
