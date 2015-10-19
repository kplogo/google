package service;

import model.DatabaseCollection;
import model.Keyword;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class KeywordParser {
	private String filename;

	public KeywordParser(String filename) {
		this.filename = filename;
		parse();
	}

	private void parse() {
		List<Keyword> documentList = DatabaseCollection.getKeywordList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String title = br.readLine().trim();
				documentList.add(new Keyword(title));
			}
		} catch (FileNotFoundException e) {
			System.out.println("No database available.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
