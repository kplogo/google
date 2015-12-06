package model;

import java.util.Map;

public class Term {
    private String value;
    private String realName;


    private Term(String value, String realName) {
        this.value = value;
        this.realName = realName;
    }

    public String getValue() {
        return value;
    }


    public static Term create(String termString, String realName) {
        Map<String, Term> termMap = DatabaseCollection.getTermMap();
        Term term = termMap.get(termString);
        if (null == term) {
            term = new Term(termString, realName);
            termMap.put(termString, term);
        }

        return term;
    }


    @Override
    public String toString() {
        return value;
    }


    public String getRealValue() {
        return realName;
    }

}
