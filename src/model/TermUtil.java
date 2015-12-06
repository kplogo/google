package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TermUtil {
    private static List<String> stopWords;

    public static List<String> getStopWords() {
        if (stopWords == null) {
            stopWords = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader("resources/stopwords.txt"));
                while (br.ready()) {
                    String line = br.readLine().trim();
                    stopWords.add(line);
                }

            } catch (FileNotFoundException e) {
                System.out.println("No database available.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stopWords;
    }

}
