package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseCollection {
	private static List<Document> documentList = new LinkedList<>();
	private static List<Keyword> keywordList = new LinkedList<>();
	private static Map<String, Term> termMap = new HashMap<>();

	public static List<Document> getDocumentList() {
		return documentList;
	}

	public static List<Keyword> getKeywordList() {
		return keywordList;
	}

	public static Map<String, Term> getTermMap() {
		return termMap;
	}

	public static void clear() {
		documentList = new LinkedList<>();
		keywordList = new LinkedList<>();
		termMap = new HashMap<>();

	}
}
