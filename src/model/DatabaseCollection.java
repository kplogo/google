package model;

import java.util.*;

public class DatabaseCollection {
    private static List<Document> documentList = new LinkedList<>();
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
    }

}
