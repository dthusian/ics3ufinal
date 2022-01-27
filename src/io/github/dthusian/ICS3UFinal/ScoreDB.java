package io.github.dthusian.ICS3UFinal;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class ScoreDB {
    public static class ScoreEntry {
        public int numPerfect;
        public int numGood;
        public int numBad;
        public int numMiss;
        public ScoreEntry(int nPerfect, int nGood, int nBad, int nMiss) {
            numPerfect = nPerfect;
            numGood = nGood;
            numBad = nBad;
            numMiss = nMiss;
        }
        @Override
        public String toString() {
            return numPerfect + "~" + numGood + "~" + numBad + "~" + numMiss;
        }
        public double accuracy() {
            return 100 * (numBad * 0.25 + numGood * 0.5 + numPerfect) / (numMiss + numBad + numGood + numPerfect);
        }
    }

    // key: beatmap id
    HashMap<String, ScoreEntry> scores;

    public ScoreDB(File file) throws IOException {
        scores = new HashMap<>();
        if(!file.exists()) {
            file.createNewFile();
            return;
        }
        Scanner s = new Scanner(new FileReader(file));
        while(s.hasNextLine()) {
            String line = s.nextLine();
            if(line.trim().length() == 0) continue;
            String[] parts = line.split("~");
            ScoreEntry ent = new ScoreEntry(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
            scores.put(parts[0], ent);
        }
        s.close();
    }

    public void saveScores(File file) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        String[] keys = scores.keySet().toArray(new String[0]);
        for(int i = 0; i < keys.length; i++) {
            writer.println(keys[i] + "~" + scores.get(keys[i]).toString());
        }
        writer.close();
    }

    public static String getKey(HashMap<String, String> metadata) {
        return metadata.get("Title") + "|" + metadata.get("Artist") + "|" + metadata.get("Creator") + "|" + metadata.get("Version");
    }
}
