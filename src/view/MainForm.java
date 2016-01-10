package view;

import model.Document;
import model.Keyword;
import service.DataService;
import service.SiblingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainForm {

    public static final int MAX_LENGTH_OF_KEYWORD = 4;
    public static final boolean PRINT_ALL_KEYWORDS_LIST = true;
    private static final boolean PRINT_RELATED_KEYWORDS_DECLARED = false;
    private static final boolean PRINT_RELATED_KEYWORDS_PARSED = false;
    private static final boolean PRINT_DOCUMENT_KEYWORDS = true;
    private List<Keyword> processedKeywords = new ArrayList<>();
    private static final boolean PRINT_DOCUMENT_DECLARED_KEYWORDS = false;

    public static void main(String[] args) {
        new MainForm();
    }

    public MainForm() {
        List<Document> allDocuments = new DataService("c:\\studia\\google\\resources\\data\\").getAllDocuments();
        //s³owa kluczowe dla okreœlonych dokumentów
        allDocuments.forEach(this::printDocument);
        //s³owa kluczowe dla zbioru dokumentów
        printRelatedKeywords(allDocuments);

        printAllKeywordList();
        System.out.println("---------------------------------------");
        System.out.println("Przetworzono plikow: " + allDocuments.size());
    }

    private void printAllKeywordList() {
        if (!PRINT_ALL_KEYWORDS_LIST) {
            return;
        }
        System.out.println("---------------------------------------");
        System.out.println("Znaleziono slow kluczowych: " + processedKeywords.size());
        System.out.println("Lista slow kluczowych: ");
        processedKeywords.stream().sorted().forEach(item -> System.out.println(item.getRealName()));
    }

    private void printRelatedKeywords(List<Document> documentList) {
        for (Document document : documentList) {
            printRelatedKeywordsParsed(documentList, document);
            printRelatedKeywordDeclared(documentList, document);
        }
    }

    private void printRelatedKeywordDeclared(List<Document> documentList, Document document) {
        for (String keyword : document.getDeclaredKeywords()) {
            Keyword preparedKeyword = Keyword.createKeywordFromString(keyword);
            processKeyword(documentList, preparedKeyword, preparedKeyword.getWordCount(), PRINT_RELATED_KEYWORDS_DECLARED);
        }
    }

    private void printRelatedKeywordsParsed(List<Document> documentList, Document document) {
        for (int i = 1; i < MAX_LENGTH_OF_KEYWORD; i++) {
            int wordCount = i;
            Map<Keyword, Integer> siblings = document.getParsedSiblings(wordCount);
            SiblingUtil.reduceSiblings(siblings, 5)
                    .forEach(entry -> processKeyword(documentList, entry.getKey(), wordCount, PRINT_RELATED_KEYWORDS_PARSED));
        }
    }

    private void processKeyword(List<Document> documentList, Keyword keyword, int wordCount, boolean print) {
        if (processedKeywords.contains(keyword)) {
            return;
        } else {
            processedKeywords.add(keyword);
        }
        if (!print) {
            return;
        }
        System.out.println("Slowo kluczowe:");
        System.out.println(keyword.getRealName());
        System.out.println("-----------------------------------------");
        for (Document document : documentList) {
            Map<Keyword, Integer> parsedSiblings = document.getParsedSiblings(wordCount);
            Integer integer = parsedSiblings.get(keyword);
            if (integer == null) {
                integer = 0;
            }
            System.out.println(integer + " : " + document.getTitle());
        }
        System.out.println("-----------------------------------------");

    }


    private void printDocument(Document document) {
        System.out.println("---------------------------DOCUMENT START----------------------------");
        System.out.println("Title: " + document.getTitle());
        System.out.println("Plik: " + document.getFilename());
        printDocumentKeywords(document);
        if (PRINT_DOCUMENT_DECLARED_KEYWORDS) {
            System.out.println("Slowa kluczowe podane w dokumencie:");
            System.out.println("----------------------------------");
            SiblingUtil.findAndLogRealSiblings(document);
        }
        System.out.println("---------------------------DOCUMENT END----------------------------");
    }

    private void printDocumentKeywords(Document document) {
        if (!PRINT_DOCUMENT_KEYWORDS) {
            return;
        }
        for (int i = 1; i < MAX_LENGTH_OF_KEYWORD; i++) {
            System.out.println("Slowa kluczowe " + i + " wyrazowe:");
            System.out.println("----------------------------------");
            SiblingUtil.logSiblingsMulti(document, i, 100);
            System.out.println();
            System.out.println();
        }
    }

}
