package view;

import model.DatabaseCollection;
import model.Document;
import model.Keyword;
import service.DocumentParser;
import service.SiblingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainForm {

    private List<Keyword> processedKeywords = new ArrayList<>();

    public static void main(String[] args) {
        new MainForm();
    }

    public MainForm() {
        String directory = "c:\\studia\\google\\resources\\data\\";
//        String directory = "c:\\studia\\google\\resources\\data\\paper9\\JSPaw2.tex";
        File file = new File(directory);
        int processedFile = processFile(file);
        DatabaseCollection.getDocumentList().forEach(this::printDocument);
        printRelatedKeywords(DatabaseCollection.getDocumentList());
        System.out.println("---------------------------------------");
        System.out.println("Znaleziono slow kluczowych: " + processedKeywords.size());
        System.out.println("Lista slow kluczowych: ");
        processedKeywords.stream().forEach(item -> System.out.println(item.getRealName()));
        System.out.println("---------------------------------------");
        System.out.println("Przetworzono plikow: " + processedFile);
    }

    private void printRelatedKeywords(List<Document> documentList) {
        for (Document document : documentList) {
            for (int i = 1; i < 5; i++) {
                int wordCount = i;
                Map<Keyword, Integer> siblings = document.getParsedSiblings(wordCount);
                SiblingUtil.reduceSiblings(siblings, 5)
                        .forEach(entry -> processKeyword(documentList, entry.getKey(), wordCount));
            }
        }
    }

    private void processKeyword(List<Document> documentList, Keyword keyword, int wordCount) {
        if (processedKeywords.contains(keyword)) {
            return;
        } else {
            processedKeywords.add(keyword);
        }
        System.out.println("Slowo kluczowe:");
        System.out.println(keyword.getRealName());
        System.out.println(keyword.getTermName());
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


    private int processFile(File file) {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList == null) {
                return 0;
            }
            int count = 0;
            for (File file1 : fileList) {
                count += processFile(file1);
            }
            return count;
        }
        if (file.getName().endsWith("tex") || file.getName().endsWith("txt")) {
            searchFile(file.getAbsolutePath());
            return 1;
        }
        return 0;
    }


    private void searchFile(String filename) {
        new DocumentParser(filename).parse();
    }

    private void printDocument(Document document) {
        System.out.println("---------------------------DOCUMENT START----------------------------");
        System.out.println("Title: " + document.getTitle());
        System.out.println("Plik: " + document.getFilename());
        for (int i = 1; i < 5; i++) {
            System.out.println("Slowa kluczowe " + i + " wyrazowe:");
            System.out.println("----------------------------------");
            SiblingUtil.logSiblingsMulti(document, i, 100);
            System.out.println();
            System.out.println();
        }
        System.out.println("Slowa kluczowe podane w dokumencie:");
        System.out.println("----------------------------------");
        SiblingUtil.findAndLogRealSiblings(document);
        System.out.println("---------------------------DOCUMENT END----------------------------");
    }

}
