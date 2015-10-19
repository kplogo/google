package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Krzysztof on 15.10.2015.
 */
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
			values.count++;
		}
	}

	public double length(Method method) {
		double sum = 0;
		for (Values values : searchText.values()) {
			sum += values.get(method)*values.get(method);
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
		return values.get(method) * queryValues.get(method);
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "Tytuł: " + title;
	}

	private void calculateTF() {

		double max = 0;
		//calculate max
		for (Values values : searchText.values()) {
			if (values.count > max) {
				max = values.count;
			}
		}
		//normalize terms
		for (Values values : searchText.values()) {
			values.normalize(max);
		}
	}

	private class Values {
		private int count;
		private double tf;
		private Term term;

		public Values(Term term) {
			this.term = term;
		}

		public double get(Method method) {
			if (method == Method.COUNT) {
				return count;
			} else if (method == Method.TF) {
				return tf;
			} else if (method == Method.IDF) {
				return tf * term.getIdfValue();
			}
			return 0;
		}

		public void normalize(double max) {
			tf = count / max;
		}
	}

	public enum Method {
		COUNT,
		TF,
		IDF
	}
}
