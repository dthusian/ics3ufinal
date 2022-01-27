package io.github.dthusian.ICS3UFinal;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Menu extends JPanel implements MouseListener, KeyListener, Runnable, MouseWheelListener, WindowListener {
    public static final int MENU_MAIN = 0;
    public static final int MENU_SONG_SELECT = 1;
    public static final int MENU_GAME = 2;
    public static final int MENU_CREDITS = 3;
    public static final int MENU_GAME_RESULTS = 4;
    public static final int MENU_GAME_PAUSED = 5;
    public int currentMenu = 0;

    // Song select vars
    ArrayList<Song> songs = new ArrayList<>();
    int lastHoveredSong = -1;
    VSRGAudio.AudioStream previewSong = null;
    int scrollOffset = 0;
    int maxScroll = 0;
    ScoreDB scores = null;

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

    // Load all songs into song select variables
    private void loadSongs() throws IOException {
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
                if (innerFiles[j].getName().substring(Math.max(innerFiles[j].getName().length() - 4, 0)).equals(".osu")) {
                    foundOsuFile = true;
                    songs.add(new Song(innerFiles[j].getAbsolutePath()));
                }
            }
            if (!foundOsuFile) {
                throw new FileNotFoundException("no .osu file");
            }
        }
        maxScroll = listFiles.length * 100 - 100;
        scores = new ScoreDB(new File("scores.txt"));
    }

    // Draws a button
    // Mouse position used to determine whether to highlight the button
    private boolean drawButton(Graphics2D g, Color col, String text, int baseX, int baseY, int width, int height, int slant, Point mousePos) {
        g.setFont(new Font("sans-serif", Font.PLAIN, 20));
        boolean hoveredOver = false;
        // highlight gradient if hovered over
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

    // Draws the main menu
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
    
    // Draws song select
    public void drawSongSelect(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Graphics2D g2d = (Graphics2D) g;
        // Draw background if available
        if(lastHoveredSong == -1) {
            g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g2d.drawImage(songs.get(lastHoveredSong).background, 0, 0, getWidth(), getHeight(), this);
        }

        // get songs folder
        if (songs.size() == 0) {
            loadSongs();
        }

        // Draw all the buttons
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

        // Draw the stats window
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(20, 500, getWidth() - 40, getHeight() - 520);
        if (hoveredSong != -1) {
            // Play the song on hover
            Song hoveredSongSong = songs.get(hoveredSong);
            if (lastHoveredSong == -1 || previewSong == null || !Objects.equals(songs.get(lastHoveredSong).metadata.get("BeatmapSetID"), hoveredSongSong.metadata.get("BeatmapSetID"))) {
                if (previewSong != null) {
                    previewSong.stop();
                }
                previewSong = VSRGAudio.loadMusic2(hoveredSongSong.audioPath, hoveredSongSong.previewTimeMs / 1000.0);
                previewSong.resume();
            }
            // Basic data about map
            HashMap<String, String> metadata = hoveredSongSong.metadata;
            g2d.setColor(new Color(255, 255, 255));
            g2d.drawString("Title: " + metadata.get("Title"), 30, 540);
            g2d.drawString("Artist: " + metadata.get("Artist"), 30, 560);
            g2d.drawString("Mapper: " + metadata.get("Creator"), 30, 580);
            g2d.drawString("Notes: " + hoveredSongSong.notes.size(), 30, 600);

            // Draw the score entry
            ScoreDB.ScoreEntry score = scores.scores.get(ScoreDB.getKey(metadata));
            if(score != null) {
                g2d.drawString("Top Score:", 30, 640);
                g2d.setColor(Util.colorPerfect);
                g2d.drawString(String.valueOf(score.numPerfect), 160, 640);
                g2d.setColor(Util.colorGood);
                g2d.drawString(String.valueOf(score.numGood), 220, 640);
                g2d.setColor(Util.colorBad);
                g2d.drawString(String.valueOf(score.numBad), 280, 640);
                g2d.setColor(Util.colorMiss);
                g2d.drawString(String.valueOf(score.numMiss), 340, 640);
                g2d.setColor(new Color(255, 255, 255));
                g2d.drawString(String.format("%.2f", score.accuracy()), 400, 640);
                g2d.drawString(String.valueOf(score.score()), 480, 640);
            } else {
                g2d.drawString("No score set", 30, 640);
            }
            // Draw the length
            int secondsRounded = (int) Math.floor(previewSong.getSecondsLength());
            int minutes = secondsRounded / 60;
            int secondsMod = secondsRounded % 60;
            g2d.drawString("Length: " + minutes + ":" + String.format("%02d", secondsMod), 30, 520);
            lastHoveredSong = hoveredSong;
        }
        
        drawButton(g2d, new Color(235, 102, 75), "Back", this.getWidth() - 220, 50, 200, 50, 20, p);
    }

    public void drawGame(Graphics g) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        render.draw(this, g);
        engine.tick();
    }
    
    // Pause
    public void drawGamePaused(Graphics g) {
    	render.draw(this, g);
    	
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.fillRect(this.getWidth() / 2 - 202, this.getHeight() / 2 - 102, 404, 204);
    	g2d.setColor(new Color(50, 50, 50));
    	g2d.fillRect(this.getWidth() / 2 - 200, this.getHeight() / 2 - 100, 400, 200);
    	
    	drawButton(g2d, new Color(70, 70, 70), "Resume", this.getWidth() / 2 - 180, this.getHeight() / 2 - 80, 360, 70, 20, myGetMousePosition());
    	drawButton(g2d, new Color(70, 70, 70), "Quit", this.getWidth() / 2 - 180, this.getHeight() / 2 + 10, 360, 70, 20, myGetMousePosition());
    }

    // Draw a game result window
    public void drawGameResults(Graphics g) {
        g.drawImage(engine.currentSong.background, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(25, 25, getWidth() - 50, getHeight() - 50);
        g.setFont(new Font("sans-serif", Font.PLAIN, 70));
        g.setColor(Util.colorPerfect);
        g.drawString(String.format("Perfect: %d", engine.numPerfect), 50, 100);
        g.setColor(Util.colorGood);
        g.drawString(String.format("Good: %d", engine.numGood), 50, 180);
        g.setColor(Util.colorBad);
        g.drawString(String.format("Bad: %d", engine.numBad), 50, 260);
        g.setColor(Util.colorMiss);
        g.drawString(String.format("Miss: %d", engine.numMiss), 50, 340);
        g.setColor(new Color(255, 255, 255));
        g.drawString(String.format("Accuracy: %.2f", engine.accuracy()), 50, 420);
        drawButton((Graphics2D)g, new Color(159, 64, 255), "Back", 50, 500, 500, 50, 20, myGetMousePosition());
    }

    public void drawInstructions(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 99), 0, this.getHeight(), new Color(9, 0, 173)));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("sans-serif", Font.PLAIN, 20));
        g2d.drawString("CS mania was made like a classic mania-type VSRG.", 50, 100);
        g2d.drawString("Each lane is assigned the keys DFJK. When the notes reach the line, tap the short notes, hold on the long notes.", 50, 125);
        g2d.drawString("Accuracy is key.", 50, 150);
        g2d.drawString("Credits:", 50, 200);
        g2d.drawString("Developers: Brendan Tam, Adrian Wu", 50, 225);
        g2d.drawString("Mapping: Brendan Tam, other osu users", 50, 250);
        drawButton(g2d, new Color(235, 102, 75), "Back", this.getWidth() - 220, 50, 200, 50, 20, myGetMousePosition());
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
            // Handle when the map is done
            // There isn't any mouse/keypress associated with this event
            if (engine.isMapFinished()) {
                engine.endMap();
                render = null;
                currentMenu = MENU_GAME_RESULTS;
                ScoreDB.ScoreEntry oldScore = scores.scores.get(ScoreDB.getKey(engine.currentSong.metadata));
                // Only save higher scores
                if(oldScore == null || engine.accuracy() > oldScore.accuracy()) {
                    scores.scores.put(ScoreDB.getKey(engine.currentSong.metadata), new ScoreDB.ScoreEntry(engine.numPerfect, engine.numGood, engine.numBad, engine.numMiss));
                    try {
                        scores.saveScores(new File("scores.txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (currentMenu == MENU_GAME_RESULTS) {
            drawGameResults(g);
        } else if (currentMenu == MENU_GAME_PAUSED) {
            drawGamePaused(g);
        } else if (currentMenu == MENU_CREDITS){
            drawInstructions(g);
        } else {
            throw new RuntimeException("Invalid menu");
        }
    }

    // Convenience method to get mouse position
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
            } else if (e.getKeyCode() == 27) {
            	currentMenu = MENU_GAME_PAUSED;
            	render.frozen = true;
            	engine.currentSong.audio.stop();
            	engine.currentSong.audio = null;
            	try {
					engine.currentSong.audio = VSRGAudio.loadMusic2(engine.currentSong.audioPath, engine.masterTime / 1000.0);
					System.out.println("epic");
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
					System.out.println("unepic");
				}
            }
            repaint();
        } else if (currentMenu == MENU_GAME_PAUSED) {
        	if (e.getKeyCode() == 27) {
	        	currentMenu = MENU_GAME;
	        	render.frozen = false;
	        	engine.startTime = engine.startTime + (System.currentTimeMillis() - (engine.masterTime + engine.startTime));
	        	engine.currentSong.audio.resume();
	        }
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
        	if (e.getX() > this.getWidth() - 220 && e.getX() < this.getWidth() - 20 && e.getY() > 50 && e.getY() < 100) {
        		currentMenu = MENU_MAIN;
        		return;
        	}
        	
            int clickedSong = -1;
            for (int i = 0; i < songs.size(); i++) {
                if (e.getX() > 50 && e.getX() < 850 && e.getY() + scrollOffset > i * 100 + 50 && e.getY() + scrollOffset < i * 100 + 150) {
                    clickedSong = i;
                }
            }
            // When song clicked, enter song
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
        } else if(currentMenu == MENU_GAME_RESULTS) {
            // Back button
            if (e.getX() > 50 && e.getX() < 550 && e.getY() > 500 && e.getY() < 550) {
                engine = null;
                currentMenu = MENU_SONG_SELECT;
            }
        } else if (currentMenu == MENU_GAME_PAUSED) {
        	if (e.getX() > this.getWidth() / 2 - 180 && e.getX() < this.getWidth() / 2 + 180 && e.getY() > this.getHeight() / 2 - 80 && e.getY() < this.getHeight() / 2 - 10) {
        		currentMenu = MENU_GAME;
	        	render.frozen = false;
	        	engine.startTime = engine.startTime + (System.currentTimeMillis() - (engine.masterTime + engine.startTime));
	        	engine.currentSong.audio.resume();
        	} else if (e.getX() > this.getWidth() / 2 - 180 && e.getX() < this.getWidth() / 2 + 180 && e.getY() > this.getHeight() / 2 + 10 && e.getY() < this.getHeight() / 2 + 80) {
        		currentMenu = MENU_SONG_SELECT;
        	}
        } else if(currentMenu == MENU_CREDITS) {
            if (e.getX() > this.getWidth() - 220 && e.getX() < this.getWidth() - 20 && e.getY() > 50 && e.getY() < 100) {
                currentMenu = MENU_MAIN;
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
        scrollOffset += e.getWheelRotation() * 80;
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