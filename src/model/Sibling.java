package model;


import java.text.Annotation;
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




    public void inc() {
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

    public Sibling getPrevious() {
        return prev;
    }

    public String getPreviousValue() {
        if (prev == null) {
            return "";
        }
        return prev.getValue().getValue();
    }

    public String getNextValue() {
        if (next == null) {
            return "";
        }
        return next.getValue().getValue();
    }

    public String getNextRealValue() {
        if (next == null) {
            return "";
        }
        return next.getValue().getRealValue();
    }

    public String getPreviousRealValue() {
        if (prev == null) {
            return "";
        }
        return prev.getValue().getRealValue();
    }

    public Sibling getNext() {
        return next;
    }
}
