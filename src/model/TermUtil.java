package model;

import service.Stemmer;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TermUtil {
	public static List<Term> normalize(String title, boolean canCreate) {
		List<Term> searchText = new LinkedList<>();
		title = title.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("[ ]+", " ").toLowerCase();
		String[] split = title.split(" ");
		Term term = null;
		for (String item : split) {
			Stemmer stemmer = new Stemmer();
			stemmer.add(item);
			stemmer.stem();
			String u = stemmer.toString();
			if (!Objects.equals(u, "")) {
				term = Term.create(u, term, canCreate);
				if (term != null) {
					searchText.add(term);
				}
			}
		}
		return searchText;
	}
}
