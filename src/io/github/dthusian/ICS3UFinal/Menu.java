package io.github.dthusian.ICS3UFinal;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Menu extends JPanel implements MouseListener, KeyListener, Runnable, MouseWheelListener, WindowListener {
    public static final int MENU_MAIN = 0;
    public static final int MENU_SONG_SELECT = 1;
    public static final int MENU_GAME = 2;
    public static final int MENU_CREDITS = 3;
    public static final int MENU_GAME_RESULTS = 4;
    int currentMenu = 0;

    // Song select vars
    ArrayList<Song> songs = new ArrayList<>();
    int lastHoveredSong = -1;
    VSRGAudio.AudioStream previewSong = null;
    int scrollOffset = 0;
    int maxScroll = 0;

    // Game vars
    VSRGEngine engine = null;
    VSRGRenderer render = null;

    public Menu() {
        super();
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(0, 0, 0));
        this.setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        new Thread(this).start();
    }

    private void loadSongs() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        String songsFolderPath = "src/io/github/dthusian/ICS3UFinal/songs";
        File songsFolder = new File(songsFolderPath);
        if (!songsFolder.exists()) {
            songsFolderPath = "songs/";
            songsFolder = new File(songsFolderPath);
            if (!songsFolder.exists()) {
                throw new Error("Could not find songs folder");
            }
        }
        File[] listFiles = songsFolder.listFiles();
        if (listFiles == null) {
            throw new RuntimeException("Songs folder not present");
        }
        for (int i = 0; i < listFiles.length; i++) {
            // get each song inside songs folder
            File[] innerFiles = listFiles[i].listFiles();
            boolean foundOsuFile = false;
            for (int j = 0; j < innerFiles.length; j++) {
                if (innerFiles[j].getName().substring(innerFiles[j].getName().length() - 4).equals(".osu")) {
                    foundOsuFile = true;
                    songs.add(new Song(innerFiles[j].getAbsolutePath()));
                }
            }
            if (!foundOsuFile) {
                throw new FileNotFoundException("no .osu file");
            }
        }
        maxScroll = listFiles.length * 100 - 100;
    }

    private boolean drawButton(Graphics2D g, Color col, String text, int baseX, int baseY, int width, int height, int slant, Point mousePos) {
        g.setFont(new Font("sans-serif", Font.PLAIN, 20));
        boolean hoveredOver = false;
        if (mousePos.x > baseX && mousePos.x < baseX + width && mousePos.y > baseY && mousePos.y < baseY + height) {
            hoveredOver = true;
            g.setPaint(new GradientPaint(0, baseY, col, 0, baseY + height, Util.colLerp(col, new Color(255, 255, 255), 0.7)));
        } else {
            g.setColor(col);
        }
        g.fillPolygon(new int[]{
                baseX + slant,
                baseX + width,
                baseX + width - slant,
                baseX
        }, new int[]{
                baseY,
                baseY,
                baseY + height,
                baseY + height
        }, 4);
        g.setColor(new Color(240, 240, 240));
        g.drawString(text, baseX + 40, baseY + 35);
        return hoveredOver;
    }

    public void drawMainMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        Point p = myGetMousePosition();
        drawButton(g2d, new Color(75, 136, 235), "Play", 50, 50, 200, 50, 20, p);
        drawButton(g2d, new Color(235, 102, 75), "Credits", 50, 150, 200, 50, 20, p);
        drawButton(g2d, new Color(136, 235, 75), "Quit", 50, 250, 200, 50, 20, p);
        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("sans-serif", Font.PLAIN, 50));
        g2d.drawString("CS Mania", 50, 400);
    }

    public void drawSongSelect(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        // get songs folder
        if (songs.size() == 0) {
            loadSongs();
        }

        Point p = myGetMousePosition();
        int scroll = -scrollOffset;
        int hoveredSong = -1;
        for (int i = 0; i < songs.size(); i++) {
            HashMap<String, String> metadata = songs.get(i).metadata;
            if (drawButton(g2d, new Color(159, 64, 255), metadata.get("Artist") + " - " + metadata.get("Title") + " [" + metadata.get("Version") + "] ", 50, 50 + scroll, 800, 50, 20, p)) {
                hoveredSong = i;
            }
            scroll += 100;
        }
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(20, 500, getWidth() - 40, getHeight() - 520);
        if (hoveredSong != -1) {
            Song hoveredSongSong = songs.get(hoveredSong);
            if (lastHoveredSong != hoveredSong) {
                if (previewSong != null) {
                    previewSong.stop();
                }
                previewSong = VSRGAudio.loadMusic2(hoveredSongSong.audioPath, hoveredSongSong.previewTimeMs / 1000.0);
                previewSong.resume();
            }
            HashMap<String, String> metadata = hoveredSongSong.metadata;
            g2d.setColor(new Color(255, 255, 255));
            g2d.drawString("Title: " + metadata.get("Title"), 30, 540);
            g2d.drawString("Artist: " + metadata.get("Artist"), 30, 560);
            g2d.drawString("OD: " + hoveredSongSong.accuracy, 30, 580);
            g2d.drawString("Notes: " + hoveredSongSong.notes.size(), 30, 600);
            int secondsRounded = (int) Math.floor(previewSong.getSecondsLength());
            int minutes = secondsRounded / 60;
            int secondsMod = secondsRounded % 60;
            g2d.drawString("Length: " + minutes + ":" + String.format("%02d", secondsMod), 30, 520);
            lastHoveredSong = hoveredSong;
        }
    }

    public void drawGame(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        render.draw(this, g);
        engine.tick();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentMenu == MENU_MAIN) {
            drawMainMenu(g);
        } else if (currentMenu == MENU_SONG_SELECT) {
            try {
                drawSongSelect(g);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
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
        if (currentMenu == MENU_GAME) {
            if (e.getKeyChar() == 'd') {
                render.keyPress(0);
            } else if (e.getKeyChar() == 'f') {
                render.keyPress(1);
            } else if (e.getKeyChar() == 'j') {
                render.keyPress(2);
            } else if (e.getKeyChar() == 'k') {
                render.keyPress(3);
            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (currentMenu == MENU_GAME) {
            if (e.getKeyChar() == 'd') {
                render.keyRelease(0);
            } else if (e.getKeyChar() == 'f') {
                render.keyRelease(1);
            } else if (e.getKeyChar() == 'j') {
                render.keyRelease(2);
            } else if (e.getKeyChar() == 'k') {
                render.keyRelease(3);
            }
            repaint();
        }
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
            } else if (e.getX() > 50 && e.getX() < 250 && e.getY() > 250 && e.getY() < 300) {
                close();
            }
        } else if (currentMenu == MENU_SONG_SELECT) {
            int clickedSong = -1;
            for (int i = 0; i < songs.size(); i++) {
                if (e.getX() > 50 && e.getX() < 850 && e.getY() + scrollOffset > i * 100 + 50 && e.getY() + scrollOffset < i * 100 + 150) {
                    clickedSong = i;
                }
            }
            if (clickedSong != -1) {
                Song clickedSongSong = songs.get(clickedSong);
                try {
                    clickedSongSong.loadAudio();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                    ex.printStackTrace();
                }
                previewSong.stop();
                previewSong = null;
                engine = new VSRGEngine(clickedSongSong);
                render = new VSRGRenderer(engine);
                currentMenu = MENU_GAME;
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
        while (true) {
            repaint();
            try {
                if (currentMenu != MENU_GAME) {
                    Thread.sleep(16);
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void close() {
        System.exit(0);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollOffset += -e.getWheelRotation() * 80;
        scrollOffset = Math.max(Math.min(scrollOffset, maxScroll), 0);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}