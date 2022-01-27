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
    public VSRGAudio.AudioStream audio;
    public String audioPath;
    public int accuracy = 5;
    public int audioLeadInMs = 0;
    public int previewTimeMs = 0;

    public Song(String path) throws IOException {
        metadata = new HashMap<>();
        notes = new ArrayList<>();
        Scanner fileReader = new Scanner(new FileReader(path));
        String signature = fileReader.nextLine();
        /*
        * This big method is for parsing osu!mania maps
        * Why? so that it's easy to import existing maps into this game
        * and so that we don't have to spend all our time mapping
        * */
        if (!signature.equals("osu file format v14") && !signature.equals("osu file format v13")) {
            throw new RuntimeException("Invalid file");
        }
        String section = "";
        while (fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            if (line.equals("") || line.startsWith("//")) {
                // do nothing
            } else if (line.charAt(0) == '[') {
                section = line.substring(1, line.length() - 1);
            } else {
                if (section.equals("General")) {
                    String[] pair = line.split(": ");
                    if (pair[0].equals("Mode")) {
                        if (!pair[1].equals("3")) {
                            throw new RuntimeException("Invalid file");
                        }
                    } else if (pair[0].equals("AudioFilename")) {
                        int index = pair[1].lastIndexOf(".");
                        Path path2 = Path.of(path).getParent().resolve(pair[1].substring(0, index) + ".wav");
                        audioPath = String.valueOf(path2);
                    } else if (pair[0].equals("AudioLeadIn")) {
                        audioLeadInMs = Integer.parseInt(pair[1]);
                    } else if (pair[0].equals("PreviewTime")) {
                        previewTimeMs = Integer.parseInt(pair[1]);
                    }
                } else if (section.equals("Editor")) {
                    // ignore (we have no editor)
                } else if (section.equals("Metadata")) {
                    String[] pair = line.split(":");
                    
                    try {
                    	metadata.put(pair[0], pair[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    	metadata.put(pair[0], "");
                    }          
                } else if (section.equals("Difficulty")) {
                    String[] pair = line.split(":");
                    if (pair[0].equals("OverallDifficulty")) {
                        accuracy = (int) Double.parseDouble(pair[1]);
                    }
                } else if (section.equals("Events")) {
                    String[] values = line.split(",");
                    if (values[0].equals("0")) {
                        if (values[2].charAt(0) == '"') {
                            values[2] = values[2].substring(1, values[2].length() - 1);
                        }
                        background = ImageIO.read(new File(Path.of(path).getParent().resolve(values[2]).toString()));
                    }
                } else if (section.equals("Timing Points")) {
                    // ignore
                } else if (section.equals("HitObjects")) {
                    // reads in the notes and adds them into an arraylist as a Note class

                    String[] noteInfo = line.split(",");
                    int lane = (int) Math.floor(Integer.parseInt(noteInfo[0]) * 4 / 512);
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

    // Load the audio
    // Not done in constructor for perf reasons
    public void loadAudio() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        audio = VSRGAudio.loadMusic2(audioPath);
    }
}
