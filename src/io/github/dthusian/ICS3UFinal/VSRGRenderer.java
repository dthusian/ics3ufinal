package io.github.dthusian.ICS3UFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;

public class VSRGRenderer {
    VSRGEngine eng;
    boolean[] keysPressed;

    public VSRGRenderer(VSRGEngine eng) {
        this.eng = eng;
        keysPressed = new boolean[4];
    }

    public void keyPress(int lane) {
        keysPressed[lane] = true;
        eng.keyPress(lane);
    }

    public void keyRelease(int lane) {
        keysPressed[lane] = false;
        eng.keyRelease(lane);
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
        final Color[] laneColors = new Color[]{
                new Color(250, 180, 240),
                new Color(120, 200, 230),
                new Color(255, 230, 130),
                new Color(160, 230, 150)
        };

        // Draw background
        g.drawImage(eng.currentSong.background, 0, 0, null);
        g.setColor(new Color(40, 40, 40));
        g.fillRect(panel.getWidth() / 2 - 150, 0, 300, panel.getHeight() - 80);

        // Draw lanes
        Graphics2D g2d = (Graphics2D) g;
        drawLane(panel, g2d, -150, 0, laneColors[0]);
        drawLane(panel, g2d, -75, 1, laneColors[1]);
        drawLane(panel, g2d, 0, 2, laneColors[2]);
        drawLane(panel, g2d, +75, 3, laneColors[3]);

        // Score display
        g.setFont(new Font("sans-serif", Font.PLAIN, 52));
        g.drawString("0", 20, 50);
        g.setFont(new Font("sans-serif", Font.PLAIN, 50));
        g.setColor(new Color(255, 255, 255));
        g.drawString("0", 20, 50);

        // Draw notes
        long time = System.currentTimeMillis() - eng.startTime;
        for (int i = eng.retireNoteI; i <= eng.dispatchNoteI; i++) {
            Note currentNote = eng.currentSong.notes.get(i);
            final int NOTE_THICKNESS = 30;
            final double APPROACH_RATE = 1.4;
            int posY = (int) ((panel.getHeight() - 80 /* hitline position */) - (currentNote.time - time /* time to line */) * APPROACH_RATE);
            int farY = (int) ((panel.getHeight() - 80 /* hitline position */) - (currentNote.endTime - time /* time to line */) * APPROACH_RATE);
            if (currentNote.clickState == 0 || currentNote.clickState == 2) {
                g.setColor(laneColors[currentNote.lane]);
            } else if (currentNote.clickState == 3) {
                g.setColor(Util.colLerp(laneColors[currentNote.lane], new Color(0, 0, 0), 0.3));
                System.out.println("amogus4");
            }
            if (currentNote.clickState != 1) // dont draw if already pressed
                g.fillRect(panel.getWidth() / 2 + -150 + currentNote.lane * 75, farY - NOTE_THICKNESS / 2, 75, (posY - farY) + NOTE_THICKNESS);
        }

        // Draw last judgement
        String judgementStr = "";
        Color judgementCol = new Color(0);
        boolean setJudgement = true;
        if (eng.lastJudgement == 0) {
            judgementStr = "Miss";
            judgementCol = new Color(120, 0, 0);
        } else if (eng.lastJudgement == 1) {
            judgementStr = "Bad";
            judgementCol = new Color(120, 0, 0);
        } else if (eng.lastJudgement == 2) {
            judgementStr = "Good";
            judgementCol = new Color(30, 150, 200);
        } else if (eng.lastJudgement == 3) {
            judgementStr = "Perfect";
            judgementCol = new Color(200, 150, 30);
        } else {
            setJudgement = false;
        }
        if (setJudgement) {
            g2d.setColor(judgementCol);
            g2d.drawString(judgementStr, (int) (panel.getWidth() / 2 - g2d.getFont().getStringBounds(judgementStr, g2d.getFontRenderContext()).getWidth() / 2), panel.getHeight() - 100);
        }
    }
}
