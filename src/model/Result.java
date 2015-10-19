package model;

public class Result {
	private final Document document;
	private final double similarity;

	public Result(Document document, double similarity) {
		this.document = document;
		int precision = 1000;
		this.similarity = Math.round(similarity * precision) * 1.0 / precision;
	}

	public Document getDocument() {
		return document;
	}

	public double getSimilarity() {
		return similarity;
	}

	@Override
	public String toString() {
		return similarity + ":" + document.getTitle();
	}
}

