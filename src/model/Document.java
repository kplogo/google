package model;

import service.SiblingUtil;
import service.Stemmer;

import java.util.*;
import java.util.stream.Stream;

public class Document {
    private final String filename;
    private final List<String> author;
    private final String title;
    private final String content;
    private final List<String> declaredKeywords;
    private final Map<SiblingKey, SiblingList> siblings = new HashMap<>();
    private Map<String, Map<Keyword, Integer>> parsedSiblings = new HashMap<>();

    public Document(String filename, List<String> author, String title, String content, String[] declaredKeywords) {
        this.filename = filename;
        this.author = author;
        this.title = title;
        this.content = content;
        this.declaredKeywords = new ArrayList<>();
        if (declaredKeywords != null) {
            for (String declaredKeyword : declaredKeywords) {
                this.declaredKeywords.add(declaredKeyword.trim());
            }
        }
        process(title);
        process(content);
    }

    private void process(String title) {
        title = title.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("[ ]+", " ").toLowerCase();
        String[] split = title.split(" ");
        Sibling sibling = null;
        for (String word : split) {
            String stemmedWord = Stemmer.stemWord(word);
            if (!Objects.equals(stemmedWord, "")) {
                Term term = Term.create(stemmedWord, word);
                sibling = SiblingUtil.create(this, sibling, term);
            }
        }
    }


    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Tytuł: " + title;
    }


    public String getContent() {
        return content;
    }

    public List<String> getDeclaredKeywords() {
        return declaredKeywords;
    }

    public SiblingList getSiblingList(Sibling prev, Term term) {
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

    public Map<SiblingKey, SiblingList> getSiblings() {
        return siblings;
    }

    public String getFilename() {
        return filename;
    }

    public Map<Keyword, Integer> getParsedSiblings(int wordCount) {
        Map<Keyword, Integer> map = parsedSiblings.get(getParsedSiblingsKey(wordCount));
        if (map == null) {
            map = SiblingUtil.getSiblingsMulti(this, wordCount);
            parsedSiblings.put(getParsedSiblingsKey(wordCount), map);
        }
        return map;
    }

    private String getParsedSiblingsKey(int wordCount) {
        return "Parsed" + wordCount;
    }

    public List<String> getAuthor() {
        return author;
    }
}
