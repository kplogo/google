package model;

public class Result {
	private final Document document;
	private final double similarity;
	private boolean markedAsGood;

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
		return ((markedAsGood) ? "TAK" : "NIE") + " - " + similarity + ":" + document.getTitle();
	}

	public boolean isMarkedAsGood() {
		return markedAsGood;
	}

	public void setMarkedAsGood(boolean markedAsGood) {
		this.markedAsGood = markedAsGood;
	}
}

