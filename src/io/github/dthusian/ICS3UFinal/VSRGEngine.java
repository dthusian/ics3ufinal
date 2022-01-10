package io.github.dthusian.ICS3UFinal;

import java.io.FileReader;
import java.io.IOException; //ioexception from osu how
import java.util.Scanner;

public class VSRGEngine {
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

                }
            }
        }
    }
}
