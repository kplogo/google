package model.document;

import model.Term;

public class Values {
	private int count;
	private double tf;
	private Term term;
	private double relevance = 1;

	public Values(Term term) {
		this.term = term;
	}

	public double get(Method method) {
		switch (method) {
			case COUNT:
				return count;
			case TF:
				return tf;
			case IDF:
				return tf * term.getIdfValue();
			default:
				return 0;
		}
	}

	public void normalize(double max) {
		tf = count / max;
	}

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public void incCount() {
		count++;
	}
}
