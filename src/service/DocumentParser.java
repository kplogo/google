package service;

import model.DatabaseCollection;
import model.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentParser {
	private static final String EMPTY = "";
	private static final String SPACE = " ";
	private static final Mode MODE_NORMAL = new Mode(0, Mode.Type.NORMAL);
	private String filename;

	public DocumentParser(String filename) {
		this.filename = filename;
	}

	public void parse() {
		List<Document> documentList = DatabaseCollection.getDocumentList();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String title = "";
			Mode actualMode = new Mode(0, Mode.Type.NORMAL);
			StringBuilder content = new StringBuilder();
			boolean titleMode = false;
			while (br.ready()) {
				String line = br.readLine().trim();
				actualMode = shouldBeIgnore(line, actualMode);
				if (actualMode.getType() != Mode.Type.NORMAL) {
					continue;
				}
				if (line.equals(EMPTY)) {
					continue;
				}

				if (line.startsWith("\\title{")) {
					line = line.replace("\\title{", "");
					titleMode = true;
				}
				if (titleMode) {
					int end = line.indexOf("}");
					int falseEnd = line.indexOf("\\}");
					if (end > 0 && end != falseEnd - 1) {
						title += line.substring(0, end);
						titleMode = false;
					} else {
						title += line + " ";
					}
					continue;
				}
				content.append(line).append(SPACE);
			}
			documentList.add(new Document(title, content.toString()));

		} catch (FileNotFoundException e) {
			System.out.println("No database available.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private Mode shouldBeIgnore(String line, Mode actualMode) {
		String ignored[] = {"%", "\\begin{table}", "\\begin{algo}", "\\begin{", "\\address{", "\\end{", "\\ead{", "\\documentclass{", "\\journal{", "\\author{", "\\section{"};
		String closeTag[] = {"", "\\end{table}", "\\end{algo}", "}", "}", "}", "}", "}", "}", "}", "}"};
		for (int i = 0; i < ignored.length; i++) {
			boolean startsWith = line.startsWith(ignored[i]);
			boolean endsWith = line.endsWith(closeTag[i]);
			if (actualMode.getType() != Mode.Type.IGNORE_START) {
				if (startsWith) {
					if (endsWith) {
						return new Mode(i, Mode.Type.IGNORE_SINGLE);
					}
					return new Mode(i, Mode.Type.IGNORE_START);
				}
			} else {
				if (actualMode.getId() == i && endsWith) {
					return new Mode(i, Mode.Type.IGNORE_STOP);
				}
			}
		}
		if (actualMode.getType() == Mode.Type.IGNORE_START) {
			return actualMode;
		}
		return MODE_NORMAL;

	}



}
