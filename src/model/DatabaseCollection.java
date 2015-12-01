package model;

import java.util.*;

public class DatabaseCollection {
	private static List<Document> documentList = new LinkedList<>();
	private static Map<SiblingKey, SiblingList> siblings = new HashMap<>();
	private static Map<String, Term> termMap = new HashMap<>();

	public static List<Document> getDocumentList() {
		return documentList;
	}


	public static Map<String, Term> getTermMap() {
		return termMap;
	}

	public static void clear() {
		documentList = new LinkedList<>();
		termMap = new HashMap<>();
		siblings = new HashMap<>();

	}

	public static List<Sibling> getSiblingList(Sibling prev, Term term) {
		SiblingKey key = new SiblingKey(prev, term);
		if (siblings.containsKey(key)) {
			return siblings.get(key);
		} else {
			Sibling sibling = new Sibling(prev, term);
			SiblingList list = new SiblingList();
			list.add(sibling);
			siblings.put(key, list);
			return list;
		}
	}

	public static Map<SiblingKey, SiblingList> getSiblings() {
		return siblings;
	}

	private static class SiblingKey {
		private Term prev;
		private Term term;

		public SiblingKey(Sibling prev, Term term) {
			if (prev != null) {
				this.prev = prev.getValue();
			}
			this.term = term;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			SiblingKey that = (SiblingKey) o;

			if (prev != null ? !prev.equals(that.prev) : that.prev != null) return false;
			return !(term != null ? !term.equals(that.term) : that.term != null);

		}

		@Override
		public int hashCode() {
			int result = prev != null ? prev.hashCode() : 0;
			result = 31 * result + (term != null ? term.hashCode() : 0);
			return result;
		}
	}

}
