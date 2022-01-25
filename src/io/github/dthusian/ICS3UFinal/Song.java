package io.github.dthusian.ICS3UFinal;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Song {
    public ArrayList<Note> notes;
    public BufferedImage background = null;
    public HashMap<String, String> metadata;
    public Clip audio;
    public int accuracy = 5;
    public int audioLeadIn = 0;

    public Song(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        metadata = new HashMap<>();
        notes = new ArrayList<>();
        Scanner fileReader = new Scanner(new FileReader(path));
        String signature = fileReader.nextLine();
        if(!signature.equals("osu file format v14")) {
            throw new RuntimeException("Invalid file");
        }
        String section = "";
        while(fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            
            if (line.equals("") || line.startsWith("//")) {
                // do nothing
            }
            else if(line.charAt(0) == '[') {
                section = line.substring(1, line.length() - 1);
            } else {
                if (section.equals("General")) {
                    String[] pair = line.split(": ");
                    if(pair[0].equals("Mode")) {
                        if(!pair[1].equals("3")) {
                            throw new RuntimeException("Invalid file");
                        }
                    } else if(pair[0].equals("AudioFilename")) {
                        Path path2 = Path.of(path).getParent().resolve(pair[1]);
                        //audio = VSRGAudio.loadMusic(path2.toString().trim());
                    } else if(pair[0].equals("AudioLeadIn")) {
                        audioLeadIn = Integer.parseInt(pair[1]);
                    }
                } else if (section.equals("Editor")) {
                    // ignore (we have no editor)
                } else if (section.equals("Metadata")) {
                    String[] pair = line.split(":");
                    metadata.put(pair[0], pair[1]);
                } else if (section.equals("Difficulty")) {
                    String[] pair = line.split(":");
                    if(pair[0].equals("OverallDifficulty")) {
                        accuracy = Integer.parseInt(pair[1]);
                    }
                } else if (section.equals("Events")) {
                    String[] values = line.split(",");
                    if(values[0].equals("0")) {
                        if(values[2].charAt(0) == '"') {
                            values[2] = values[2].substring(1, values[2].length() - 1);
                        }
                        background = ImageIO.read(new File(Path.of(path).getParent().resolve(values[2]).toString()));
                    }
                } else if (section.equals("Timing Points")) {
                    // ignore
                } else if (section.equals("HitObjects")) {
                    // reads in the notes and adds them into an arraylist as a Note class

                    String[] noteInfo = line.split(",");
                    int lane = (int)Math.floor(Integer.parseInt(noteInfo[0]) * 4 / 512);
                    int time = Integer.parseInt(noteInfo[2]);
                    int type = Integer.parseInt(noteInfo[3]);
                    int endTime;

                    if (type == 0b10000000) {
                        endTime = Integer.parseInt(noteInfo[5].split(":")[0]);
                    } else {
                        endTime = time;
                    }
                    
                    notes.add(new Note(time, lane, type, endTime));
                }
            }
        }
    }
    
    public void dimBg(float factor, float offset) {
    	RescaleOp rescaleOp = new RescaleOp(factor, offset, null);
    	rescaleOp.filter(this.background, this.background);
    }
    
}
