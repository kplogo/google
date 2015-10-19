package model;

import java.util.LinkedList;
import java.util.List;

public class Keyword {
	private String value;
	private List<Term> searchText = new LinkedList<>();

	public String getValue() {
		return value;
	}

	public Keyword(String value) {
		this.value = value;
		searchText.addAll(TermUtil.normalize(value, true));
	}

	public List<Term> getSearchText() {
		return searchText;
	}

	@Override
	public String toString() {
		return "Keyword: " + value;
	}
}
