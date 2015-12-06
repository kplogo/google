package model;

public class Keyword {
    private String termName;
    private String realName;

    public Keyword(String termName, String realName) {
        this.termName = termName;
        this.realName = realName;
    }

    public String getTermName() {
        return termName;
    }

    public String getRealName() {
        return realName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword keyword = (Keyword) o;

        return !(termName != null ? !termName.equals(keyword.termName) : keyword.termName != null);

    }

    @Override
    public int hashCode() {
        return termName != null ? termName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return  termName ;
    }
}
