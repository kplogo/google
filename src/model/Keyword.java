package model;

import service.Stemmer;

import java.util.Objects;

public class Keyword implements Comparable<Keyword> {
    private String termName;
    private String realName;

    public Keyword(String termName, String realName) {
        this.termName = termName;
        this.realName = realName;
    }

    public static Keyword createKeywordFromString(String keyword) {
        String[] words = keyword.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : words) {
            String stemmedWord = Stemmer.stemWord(part);
            if (!Objects.equals(stemmedWord, "")) {
                sb.append(stemmedWord)
                        .append(" ");
            }
        }
        String key = sb.toString().trim().toLowerCase();
        return new Keyword(key, keyword);
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
        return termName;
    }

    public int getWordCount() {
        return termName.split(" ").length;
    }

    @Override
    public int compareTo(Keyword o) {
        if (o == null) {
            return 1;
        }
        return this.getRealName().compareTo(o.getRealName());
    }
}
