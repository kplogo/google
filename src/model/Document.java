package model;

import model.document.Method;
import model.document.Values;

import java.util.HashMap;
import java.util.Map;

public class Document {
	private String title;
	private String content;
	private Map<Term, Values> searchText = new HashMap<>();

	public Document(String title, String content) {
		this(title, content, true);
	}

	public Document(String title, String content, boolean addToDatabase) {
		this.title = title;
		this.content = content;
		process(title, addToDatabase);
		process(content, addToDatabase);
		calculateTF();
	}

	private void process(String title, boolean addToDatabase) {
		for (Term term : TermUtil.normalize(title, false)) {
			Values values = searchText.get(term);
			if (values == null) {
				values = new Values(term);
				searchText.put(term, values);
				if (addToDatabase) {
					term.incDocumentCount();
				}
			}
			values.incCount();
		}
	}

	public double length(Method method) {
		double sum = 0;
		for (Values values : searchText.values()) {
			sum += values.get(method) * values.get(method);
		}
		return Math.sqrt(sum);
	}

	public double similarity(Document query, Method method) {
		double sum = 0;
		for (Map.Entry<Term, Values> termEntry : searchText.entrySet()) {
			sum += getValue(query.searchText.get(termEntry.getKey()), termEntry.getValue(), method);
		}
		double length = query.length(method) * length(method);
		if (length == 0) {
			return -1;
		}
		return sum / length;
	}

	private double getValue(Values queryValues, Values values, Method method) {
		if (values == null || queryValues == null) {
			return 0;
		}
		return values.get(method) * queryValues.get(method) * queryValues.getRelevance();
	}

	public double getTermRelevance(Term term) {
		Values values = searchText.get(term);
		if (values == null) {
			return 0;
		}
		return values.getRelevance();
	}

	public void setTermRelevance(Term term, double relevance) {
		if (relevance == 0) {
			return;
		}
		Values values = searchText.get(term);
		if (values == null) {
			values = new Values(term);
			searchText.put(term, values);
		}
		if (relevance < 0) {
			relevance = 0;
		}
		values.setRelevance(relevance);
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "Tytuł: " + title;
	}

	private void calculateTF() {

		double max = 0;
		//calculate max
		for (Values values : searchText.values()) {
			if (values.get(Method.COUNT) > max) {
				max = values.get(Method.COUNT);
			}
		}
		//normalize terms
		for (Values values : searchText.values()) {
			values.normalize(max);
		}
	}

	public double getTermCount(Term term) {
		Values values = searchText.get(term);
		if (values == null) {
			return 0;
		}
		return values.get(Method.COUNT);

	}

	public String getQueryText() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Term, Values> entry : searchText.entrySet()) {
			sb.append(entry.getKey().getValue())
					.append(":")
					.append(entry.getValue().getRelevance())
					.append(" ");
		}
		return sb.toString();
	}


	public String getContent() {
		return content;
	}
}
