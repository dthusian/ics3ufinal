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

    public void draw(JPanel panel, Graphics g) {
        g.drawImage(eng.currentSong.background, 0, 0, null);
        g.setColor(new Color(124, 124, 124));
        g.fillRect(panel.getWidth()/2 - 150, 0, 300, panel.getHeight() - 85);
        g.setColor(new Color(211, 211, 211));
        g.fillRect(panel.getWidth()/2 - 150, panel.getHeight() - 85, 300, 35);

        g.setColor(new Color(250, 180, 240));
        g.fillRect(panel.getWidth()/2 - 150, panel.getHeight() - 50, 78, 50);
        g.setColor(new Color(160, 230, 150));
        g.fillRect(panel.getWidth()/2 + 78, panel.getHeight() - 50, 75, 50);
        g.setColor(new Color(120, 200, 230));
        g.fillRect(panel.getWidth()/2 - 72, panel.getHeight() - 50, 75, 50);
        g.setColor(new Color(255, 230, 130));
        g.fillRect(panel.getWidth()/2 + 3, panel.getHeight() - 50, 75, 50);

        g.setColor(new Color(150, 150, 150));
        g.fillRect(panel.getWidth()/2 - 150, 0, 5, panel.getHeight());
        g.fillRect(panel.getWidth()/2 - 75, 0, 5, panel.getHeight() - 50);
        g.fillRect(panel.getWidth()/2, 0, 5, panel.getHeight() - 50);
        g.fillRect(panel.getWidth()/2 + 75, 0, 5, panel.getHeight() - 50);
        g.fillRect(panel.getWidth()/2 + 150, 0, 5, panel.getHeight());
        g.fillRect(panel.getWidth()/2 - 150, panel.getHeight() - 85, 300, 5);
        g.fillRect(panel.getWidth()/2 - 150, panel.getHeight() - 50, 300, 5);

        g.setFont(new Font("sans-serif", Font.PLAIN, 52));
        g.drawString("0", 20, 50);
        g.setFont(new Font("sans-serif", Font.PLAIN, 50));
        g.setColor(new Color(255, 255, 255));
        g.drawString("0", 20, 50);

        if (this.keysPressed[0]) {
            g.setColor(new Color(250, 180, 240));
            g.fillRect(panel.getWidth()/2 - 145, panel.getHeight() - 80, 70, 30);
        }
        if (this.keysPressed[1]) {
            g.setColor(new Color(120, 200, 230));
            g.fillRect(panel.getWidth()/2 - 70, panel.getHeight() - 80, 70, 30);
        }
        if (this.keysPressed[2]) {
            g.setColor(new Color(255, 230, 130));
            g.fillRect(panel.getWidth()/2 + 5, panel.getHeight() - 80, 70, 30);
        }
        if (this.keysPressed[3]) {
            g.setColor(new Color(160, 230, 150));
            g.fillRect(panel.getWidth()/2 + 80, panel.getHeight() - 80, 70, 30);
        }
    }
}
