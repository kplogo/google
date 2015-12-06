package model;

public class SiblingKey {
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

        return !(prev != null ? !prev.equals(that.prev) : that.prev != null) && !(term != null ? !term.equals(that.term) : that.term != null);

    }

    @Override
    public int hashCode() {
        int result = prev != null ? prev.hashCode() : 0;
        result = 31 * result + (term != null ? term.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return prev.getRealValue() + " " + term.getRealValue();
    }
}
