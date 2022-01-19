package io.github.dthusian.ICS3UFinal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class FileLoader {
	public HashMap<String, HashMap<String, String>> readOsuFile(String path) throws IOException {
		// converts osu file into readable format by program, split into sections
		HashMap<String, HashMap<String, String>> osuFile = new HashMap<>();
		
		Scanner fileReader = new Scanner(new FileReader(path));
        String signature = fileReader.nextLine();
        if(!signature.equals("osu file format v14")) {
            throw new RuntimeException("Invalid file");
        }
        String section = "";
        while(fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            if(line.charAt(0) == '[') {
                section = line.substring(1, line.length() - 1);
                osuFile.put(section, new HashMap<>());
            } else if (line == "") {
            	continue;
            } else {
            	
            	// reads in all .osu file data
            	
                if (section.equals("General")) {
                	
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
                    
                }
            }
        }
        
        return osuFile;
	}
	
	public String[] getBackground(String path) throws IOException {
		// looks for line containing bg information and returns that
		Scanner fileReader = new Scanner(new FileReader(path));
		String signature = fileReader.nextLine();
        if(!signature.equals("osu file format v14")) {
            throw new RuntimeException("Invalid file");
        }
        boolean bg = false;
        while(fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            if (line == "//Background and Video events") {
            	bg = true;
            } else if (bg) {
            	if (line.charAt(0) == '0' && line.charAt(1) == ',') {
            		String[] bgInfo = line.split(",");
            		String[] background = {bgInfo[2], bgInfo[3], bgInfo[4]};
            		return background;
            	}
            }
        }   
        return null;
	}
	
	public String[] getTitleArtist(String path) throws IOException {
		// looks for line containing artist and title information and returns that
		Scanner fileReader = new Scanner(new FileReader(path));
		String signature = fileReader.nextLine();
        if(!signature.equals("osu file format v14")) {
            throw new RuntimeException("Invalid file");
        }
        String[] titleArtist = new String[2];
        while(fileReader.hasNext()) {
            String line = fileReader.nextLine().trim();
            if (line.split(":")[0].equals("Title")) {
            	titleArtist[0] = line.split(":")[1];
            	if (titleArtist[0] != null && titleArtist[1] != null) return titleArtist;
            } else if (line.split(":")[0].equals("Artist")) {
            	titleArtist[1] = line.split(":")[1];
            	if (titleArtist[0] != null && titleArtist[1] != null) return titleArtist;
            }
        }
		
		return null;
	}
	
	public FileLoader() {
		
	}
}
