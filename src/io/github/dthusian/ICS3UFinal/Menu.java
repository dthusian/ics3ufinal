package io.github.dthusian.ICS3UFinal;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Menu extends JPanel implements MouseListener, KeyListener, Runnable {
  public static final int MENU_MAIN = 0;
  public static final int MENU_SONG_SELECT = 1;
  public static final int MENU_GAME = 2;
  public static final int MENU_CREDITS = 3;
  public static final int MENU_GAME_RESULTS = 4;
  int currentMenu = 2;

  public Menu() {
    super();
    setPreferredSize(new Dimension(1280, 720));
    setBackground(new Color(0, 0, 0));
    addMouseListener(this);
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
  
  public void drawSongSelect(Graphics g) throws IOException {
	  Graphics2D g2d = (Graphics2D)g;
	  g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
	  g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	  FileLoader fileLoader = new FileLoader();
	  
	  // get songs folder
	  String songsFolderPath = "src\\io\\github\\dthusian\\ICS3UFinal\\songs";
	  File songsFolder = new File(songsFolderPath);
	  File[] songsFiles = songsFolder.listFiles();
	  
	  /*
	  // load map background as song select background
	  try {
		  BufferedImage image = ImageIO.read(new File(songFolderPath + "\\" + songFiles[0].getName()));
	  } catch (IOException e) {
		  
	  }
	  */
	  
	  Point p = myGetMousePosition();
	  int scroll = 50;
	  for (int i = 0; i < songsFiles.length; i++) {
		  
		  // get each song inside songs folder
		  File songFolder = new File(songsFolderPath + "\\" + songsFiles[i].getName());
		  File[] songFiles = songFolder.listFiles();
		  String osuFilePath = null;
		  for (int j = 0; j < songFiles.length; j++) {
			  if (songFiles[j].getName().substring(songFiles[j].getName().length() - 4).equals(".osu")) {
				  osuFilePath = songsFolderPath + "\\" + songFolder.getName() + "\\" + songFiles[j].getName();
			  }
		  }
		  if (osuFilePath == null) {
			  throw new FileNotFoundException("no .osu file");
		  }
		  
		  drawButton(g2d, new Color(159, 64, 255), fileLoader.getTitleArtist(osuFilePath)[1] + " - " + fileLoader.getTitleArtist(osuFilePath)[0], 50, scroll, 200, 50, 20, p);
		  scroll += 100;
	  }
	  
	  new Thread(this).start();
  }

  public void drawGame(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
	  Graphics2D g2d = (Graphics2D)g;
	  g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
	  g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	  FileLoader fileLoader = new FileLoader();
	  
	  // testing purposes
	  Song testSong = new Song("src\\io\\github\\dthusian\\ICS3UFinal\\songs\\psimissing\\Mami Kawada - PSI-missing (TV Size) (PotatoDew) [[4K] Insane].osu");
	  VSRGEngine engine = new VSRGEngine(testSong);
	  Note testNote = new Note(3000, 2, 0, 0);
	  
	  testSong.dimBg(0.5f, 0.0f);
	  g.drawImage(testSong.background, 0, 0, null);
	  gameLayout(g2d);
	  
	  new Thread(this).start();
  }
  
  public void gameLayout(Graphics g) {
	  g.setColor(new Color(211, 211, 211));
	  g.fillRect(this.getWidth()/2 - 150, 0, 300, this.getHeight());
	  
	  g.setColor(new Color(0, 0, 0));
	  g.fillRect(this.getWidth()/2 - 150, 0, 5, this.getHeight());
	  g.fillRect(this.getWidth()/2 - 75, 0, 5, this.getHeight());
	  g.fillRect(this.getWidth()/2, 0, 5, this.getHeight());
	  g.fillRect(this.getWidth()/2 + 75, 0, 5, this.getHeight());
	  g.fillRect(this.getWidth()/2 + 150, 0, 5, this.getHeight());
	  g.fillRect(this.getWidth()/2 - 150, this.getHeight() - 105, 300, 5);
	  g.fillRect(this.getWidth()/2 - 150, this.getHeight() - 50, 300, 5);
	  
	  g.setFont(new Font("sans-serif", Font.PLAIN, 52));
	  g.drawString("0", 20, 50);
	  g.setFont(new Font("sans-serif", Font.PLAIN, 50));
	  g.setColor(new Color(255, 255, 255));
	  g.drawString("0", 20, 50);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(currentMenu == MENU_MAIN) {
      drawMainMenu(g);
    } else if (currentMenu == MENU_SONG_SELECT) {
    	try {
			drawSongSelect(g);
		} catch (IOException e) {
			e.printStackTrace();
		}
    } else if (currentMenu == MENU_GAME) {
    	try {
			drawGame(g);
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		}
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
	  if (currentMenu == MENU_MAIN) {
		  if (e.getX() > 50 && e.getX() < 250 && e.getY() > 50 && e.getY() < 100) {
			  currentMenu = MENU_SONG_SELECT;
		  } else if (e.getX() > 50 && e.getX() < 250 && e.getY() > 150 && e.getY() < 200) {
			  currentMenu = MENU_CREDITS;
		  }
	  }
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