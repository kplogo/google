package model;

import service.Stemmer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TermUtil {
	private static List<String> stopWords;

	public static List<Term> normalize(String title, boolean canCreate) {
		loadStopWords("resources/stopwords.txt");
		List<Term> searchText = new LinkedList<>();
		title = title.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("[ ]+", " ").toLowerCase();
		String[] split = title.split(" ");
		Term term = null;
		Sibling sibling = null;
		for (String item : split) {
			if (stopWords.contains(item)) {
				continue;
			}
			Stemmer stemmer = new Stemmer();
			stemmer.add(item);
			stemmer.stem();
			String u = stemmer.toString();
			if (!Objects.equals(u, "")) {
				term = Term.create(u, term);
				sibling = Sibling.create(sibling, term);
				if (term != null) {
					searchText.add(term);
				}
			}
		}
		return searchText;
	}

	private static void loadStopWords(String filename) {
		if (stopWords == null) {
			stopWords = new ArrayList<>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(filename));
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
	}

}
