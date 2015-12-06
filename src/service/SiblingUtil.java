package service;

import model.*;

import java.util.*;
import java.util.stream.Stream;

public class SiblingUtil {
    public static final int TOLERANCE = 50;

    public static Sibling create(Document document, Sibling previous, Term actual) {
        Sibling selected = null;
        List<Sibling> siblings = document.getSiblingList(previous, actual);
        if (previous != null) {
            for (Sibling sibling : siblings) {
                if (previous.getNext() != null && previous.getNext() == sibling) {
                    selected = sibling;
                    break;
                }
            }
        }
        if (selected == null) {
            for (Sibling sibling : siblings) {
                if (sibling.getNext() == null) {
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

    public static void logSiblingsMulti(Document document, int count, int limit) {
        Map<Keyword, Integer> map = getSiblingsMulti(document, count);
        reduceSiblings(map, limit)
                .forEach(entry -> System.out.println(
                        entry.getKey().getRealName() + ": " + entry.getValue()));
    }

    public static Stream<Map.Entry<Keyword, Integer>> reduceSiblings(Map<Keyword, Integer> stream, int limit) {
        int maxCount = getMaxCountInt(stream.entrySet().stream());
        double toleranceValue = countTolerance(maxCount);
        return stream.entrySet().stream()
                .sorted(((o1, o2) -> o2.getValue() - o1.getValue()))
                .filter(entry -> entry.getValue() > toleranceValue)
                .limit(limit);
    }

    public static void findAndLogRealSiblings(Document document) {
        List<String> declaredKeywords = document.getDeclaredKeywords();
        for (String declaredKeyword : declaredKeywords) {
            String siblingForKeyword = findSiblingForKeyword(document, declaredKeyword);
            if (siblingForKeyword != null) {
                System.out.println(siblingForKeyword);
            }
        }
    }

    private static Keyword getSiblingKey(int count, Sibling firstSibling) {
        String termLine = "";
        String realLine = "";
        Sibling sibling = firstSibling;

        for (int i = 0; i < count; i++) {
            if (sibling == null) {
                return null;
            }
            realLine += " " + sibling.getValue().getRealValue();
            termLine += " " + sibling.getValue().getValue();
            sibling = sibling.getNext();
        }
        return new Keyword(termLine.trim(), realLine.trim());
    }

    public static Map<Keyword, Integer> getSiblingsMulti(Document document, int count) {
        Map<Keyword, Integer> map = new HashMap<>();
        for (SiblingList siblings : document.getSiblings().values()) {
            for (Sibling sibling : siblings) {
                Keyword line = getSiblingKey(count, sibling.getPrevious());
                if (line == null) {
                    continue;
                }
                Integer lineValue = map.get(line);
                if (lineValue == null) {
                    map.put(line, sibling.getCount());
                } else {
                    map.put(line, lineValue + sibling.getCount());
                }
            }
        }
        return map;
    }

    private static double countTolerance(int maxCount) {
        return maxCount - maxCount / 100.0 * TOLERANCE;
    }

    private static int getMaxCountInt(Stream<Map.Entry<Keyword, Integer>> values) {
        return values
                .max((o1, o2) -> o1.getValue() - o2.getValue())
                .get()
                .getValue();
    }

    private static String findSiblingForKeyword(Document document, String keyword) {
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
        Map<Keyword, Integer> siblingsMulti = getSiblingsMulti(document, key.split(" ").length);
        Integer count = siblingsMulti.get(new Keyword(key,""));
        if (count == null) {
            count = 0;
        }
        return keyword + ": " + count;
    }
}
