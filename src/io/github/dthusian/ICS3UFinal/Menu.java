package io.github.dthusian.ICS3UFinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Menu extends JPanel implements MouseListener, KeyListener, Runnable {
  public static final int MENU_MAIN = 0;
  public static final int MENU_SONG_SELECT = 1;
  public static final int MENU_GAME = 2;
  public static final int MENU_CREDITS = 3;
  public static final int MENU_GAME_RESULTS = 4;
  int currentMenu = 0;

  public Menu() {
    super();
    setPreferredSize(new Dimension(600, 800));
    setBackground(new Color(0, 0, 0));
  }

  private void drawButton(Graphics g, Color col, String text, int baseX, int baseY, int width, int height, int slant, Point mousePos) {
    int shift = 0;
    if(mousePos.x > baseX && mousePos.x < baseX + width && mousePos.y > baseY && mousePos.y < baseY + height) {
      shift = 30;
    }
    g.setFont(new Font("sans-serif", Font.PLAIN, 20));
    g.setColor(col);
    g.fillPolygon(new int[] {
            baseX + slant + shift,
            baseX + width + shift,
            baseX + width - slant + shift,
            baseX + shift
    }, new int[] {
            baseY,
            baseY,
            baseY + height,
            baseY + height
    }, 4);
    g.setColor(new Color(240, 240, 240));
    g.drawString(text, baseX + 40 + shift, baseY + 35);
  }

  public void drawMainMenu(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
    Point p = myGetMousePosition();
    drawButton(g2d, new Color(75, 136, 235), "Play", 50, 50, 200, 50, 20, p);
    drawButton(g2d, new Color(235, 102, 75), "Credits", 50, 150, 200, 50, 20, p);
    drawButton(g2d, new Color(136, 235, 75), "Quit", 50, 250, 200, 50, 20, p);
    g2d.setColor(new Color(240, 240, 240));
    g2d.setFont(new Font("sans-serif", Font.PLAIN, 50));
    g2d.drawString("CS Mania", 50, 400);
    new Thread(this).start();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(currentMenu == MENU_MAIN) {
      drawMainMenu(g);
    } else {
      throw new RuntimeException("Invalid menu");
    }
  }

  public int getCurrentMenu() {
    return currentMenu;
  }

  private Point myGetMousePosition() {
    Point p = MouseInfo.getPointerInfo().getLocation();
    SwingUtilities.convertPointFromScreen(p, this);
    return p;
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {

  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

  @Override
  public void mousePressed(MouseEvent e) {

  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }

  @Override
  public void run() {
    while(true) {
      repaint();
      try {
        Thread.sleep(16);
      } catch(InterruptedException ignored) { }
    }
  }
}