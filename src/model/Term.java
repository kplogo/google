package model;

import view.MainForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Term {
	private String value;
	private Map<Term, Integer> previousValue = new HashMap<>();
	private Map<Term, Integer> nextValue = new HashMap<>();
	private int documentsCount;
	private int totalCount;


	private Term(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public double getIdfValue() {
		if (documentsCount==0){
			return 0;
		}
		return Math.log(DatabaseCollection.getDocumentList().size() / documentsCount);
	}

	public int getDocumentsCount() {
		return documentsCount;
	}

	public static Term create(String termString, Term previousTerm, boolean canCreate) {
		Map<String, Term> termMap = DatabaseCollection.getTermMap();
		Term term = termMap.get(termString);
		if (null == term && (canCreate || !MainForm.isKeywordsEnabled())) {
			term = new Term(termString);
			termMap.put(termString, term);
		}
		term.totalCount++;
		if (previousTerm != null) {
			setSiblings(previousTerm, term.previousValue);
			setSiblings(term, previousTerm.nextValue);
		}
		return term;
	}

	private static void setSiblings(Term value, Map<Term, Integer> siblingsMap) {
		Integer count = siblingsMap.get(value);
		if (count == null) {
			siblingsMap.put(value, 1);
		} else {
			siblingsMap.put(value, count + 1);
		}
	}

	public List<Siblings> getMostPopularPrevious() {
		return getMostPopular(previousValue);
	}

	public List<Siblings> getMostPopularNext() {
		return getMostPopular(nextValue);
	}

	private List<Siblings> getMostPopular(Map<Term, Integer> siblingsMap) {
		List<Siblings> siblings = new ArrayList<>();
		for (Map.Entry<Term, Integer> siblingsEntry : siblingsMap.entrySet()) {
			Integer value = siblingsEntry.getValue();
			if (checkPhraseLimit(value)) {
				siblings.add(new Siblings(siblingsEntry.getKey(), siblingsEntry.getValue()));
			}
		}
		return siblings;
	}

	private boolean checkPhraseLimit(int value) {
		return totalCount / 5 < value && value >5;
	}


	public void incDocumentCount() {
		documentsCount++;
	}

	@Override
	public String toString() {
		return value;
	}
	public String getSiblingsInfo(){
		List<Siblings> previousValue = getMostPopularPrevious();
		List<Siblings> nextValue = getMostPopularNext();
		StringBuilder sb = new StringBuilder();
		for (Siblings siblings : previousValue) {
			sb.append(siblings).append(" ").append(value).append("\n");
		}
		for (Siblings siblings : nextValue) {
			sb.append(value).append(" ").append(siblings).append("\n");
		}
		return sb.toString();
	}

	private class Siblings {
		private Term term;
		private int count;

		public Siblings(Term term, int count) {

			this.term = term;
			this.count = count;
		}

		@Override
		public String toString() {
			return term.value + "(" + count + ")";
		}
	}
}
