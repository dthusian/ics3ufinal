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
            	
            	// reads in all .osu file data
            	
                if(section.equals("General")) {

                } else if (section.equals("Editor")) {
                    
                } else if (section.equals("Metadata")) {
                
                } else if (section.equals("Difficulty")) {
                    
                } else if (section.equals("Events")) {
                    
                } else if (section.equals("Timing Points")) {
                    
                } else if (section.equals("Hit Objects")) {
                	
                	// reads in the notes and adds them into an arraylist as a Note class
                	
                    String[] noteInfo = line.split(",");
                    int lane = (int)Math.floor(Integer.parseInt(noteInfo[0]) * 4 / 512);
                    int time = Integer.parseInt(noteInfo[2]);
                    int type = Integer.parseInt(noteInfo[3]);
                    int endTime;
                    
                    if (type == 7) {
                        endTime = Integer.parseInt(noteInfo[5]); // might not work with the colon stuff
                    } else {
                        endTime = time;
                    }
                    
                    notes.add(new Note(time, lane, type, endTime));
                }
            }
        }
        
        Timer mapTimer = new Timer();
        
        /* have each note as an individual timer that does something when its time
        for it to be drawn on screen/hit
        */
        for (int i = 0; i < notes.size(); i++) { // idk how this will work with large number of notes
            mapTimer.schedule(notes.get(i).timer, notes.get(i).time);
        }
    }
}
