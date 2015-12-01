package model;

public class Result implements Comparable {
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

	@Override
	public int compareTo(Object other) {
		if (!other.getClass().getSimpleName().equals("Result")) {
			return -1;
		}
		double diff = this.getSimilarity() - ((Result) other).getSimilarity();
		if (diff < 0) {
			return 1;
		} else if (diff > 0) {
			return -1;
		}
		return 0;
	}
}

