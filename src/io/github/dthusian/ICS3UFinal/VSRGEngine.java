package io.github.dthusian.ICS3UFinal;

import java.io.FileReader;
import java.io.IOException; //ioexception from osu how
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VSRGEngine {
    
    public static long startTime = 0;
    public static ArrayList<Note> notes = new ArrayList<>();
    
    public VSRGEngine(String filepath) throws IOException, RuntimeException {
        Scanner fileReader = new Scanner(new FileReader(filepath));
        String signature = fileReader.nextLine();
        if(!signature.equals("osu file format v14")) {
            throw new RuntimeException("Invalid file");
        }
        String section = "";
        while(fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            if(line.charAt(0) == '[') {
                section = line.substring(1, line.length() - 1);
            } else {
                if(section.equals("General")) {

                } else if (section.equals("Editor")) {
                    
                } else if (section.equals("Metadata")) {
                
                } else if (section.equals("Difficulty")) {
                    
                } else if (section.equals("Events")) {
                    
                } else if (section.equals("Timing Points")) {
                    
                } else if (section.equals("Hit Objects")) {
                    String[] noteInfo = line.split(",");
                    int lane = Math.floor(Integer.parseInt(noteInfo[0]) * 4 / 512);
                    int time = Integer.parseInt(noteInfo[2]);
                    notes.add(new Note(time, lane));
                }
            }
        }
        
        Timer mapTimer = new Timer();
        
        for (int i = 0; i < notes.length; i++) {
            mapTimer.schedule(notes[i].timer);
        }
    }
}
