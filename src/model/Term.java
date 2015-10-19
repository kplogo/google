package model;

import view.MainForm;

import java.util.Map;

public class Term {
	private String value;
	private int documentsCount;

	private Term(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public double getIdfValue() {
		return Math.log(DatabaseCollection.getDocumentList().size() / documentsCount);
	}

	public int getDocumentsCount() {
		return documentsCount;
	}

	public static Term create(String termString, boolean canCreate) {
		Map<String, Term> termMap = DatabaseCollection.getTermMap();
		Term term = termMap.get(termString);
		if (null == term && (canCreate || MainForm.ONLY_KEYWORDS_DISABLED)) {
			term = new Term(termString);
			termMap.put(termString, term);
		}
		return term;
	}

	public void incDocumentCount() {
		documentsCount++;
	}

	@Override
	public String toString() {
		return value;
	}
}
