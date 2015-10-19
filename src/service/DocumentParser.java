package service;

import model.DatabaseCollection;
import model.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DocumentParser {
	private String filename;

	public DocumentParser(String filename) {
		this.filename = filename;
		parse();
	}
	private void parse(){
		List<Document> documentList = DatabaseCollection.getDocumentList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String title = br.readLine().trim();
				StringBuilder content = new StringBuilder();
				while (br.ready()) {
					String line = br.readLine().trim();
					if(line.equals("")){
						break;
					}
					content.append(line).append(" ");
				}
				documentList.add(new Document(title,content.toString()));
			}

		} catch (FileNotFoundException e) {
			System.out.println("No database available.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
