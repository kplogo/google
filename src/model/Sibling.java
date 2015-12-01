package model;


import java.util.List;

public class Sibling {
	private final Sibling prev;
	private Sibling next;
	private final Term value;
	private int count = 0;

	public Sibling(Sibling prev, Term value) {
		this.prev = prev;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Sibling sibling = (Sibling) o;

		return !(value != null ? !value.equals(sibling.value) : sibling.value != null);

	}

	@Override
	public String toString() {
		return getJson(false);

	}

	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}


	public static Sibling create(Sibling previous, Term actual) {
		Sibling selected = null;
		List<Sibling> siblings = DatabaseCollection.getSiblingList(previous, actual);
		if (previous != null) {
			for (Sibling sibling : siblings) {
				if (previous.next != null && previous.next == sibling) {
					selected = sibling;
					break;
				}
			}
		}
		if (selected == null) {
			for (Sibling sibling : siblings) {
				if (sibling.next == null) {
					selected = sibling;
					break;
				}
			}
		}

		if (selected == null) {
			selected = new Sibling(previous, actual);
			siblings.add(selected);
		}

		if (previous != null) {
			previous.setNext(selected);
		}

		selected.inc();
		return selected;
	}

	private void inc() {
		count++;
	}

	public void setNext(Sibling next) {
		this.next = next;
	}

	public String getJson(boolean full) {
		Object prevText = null;
		if (prev != null) {
			if (full) {
				prevText = prev.getJson(false);
			} else {
				prevText = prev.value;
			}
		}
		Object nextValue = null;
		if (next != null) {
			if (full) {
				nextValue = next.getJson(false);
			} else {
				nextValue = next.value;
			}
		}
		return "{" + prevText +
				" " + value +
				" " + nextValue +
				": " + count + "}";
	}

	public Term getValue() {
		return value;
	}

	public int getCount() {
		return count;
	}
}
