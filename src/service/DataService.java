package service;

import model.DatabaseCollection;
import model.Document;

import java.io.File;
import java.util.List;

/**
 * Created by Krzysztof.Pawlak on 2016-01-10.
 */
public class DataService {
    String directory;

    public DataService(String directory) {
        this.directory = directory;
    }

    public List<Document> getAllDocuments() {
        DatabaseCollection.clear();
        File file = new File(directory);
        processFile(file);
        return DatabaseCollection.getDocumentList();
    }

    public List<Document> getSelectedDocuments(List<String> fileNames) {
        DatabaseCollection.clear();
        for (String fileName : fileNames) {
            searchFile(fileName);
        }
        return DatabaseCollection.getDocumentList();
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
        if (file.getName().toLowerCase().endsWith("tex") || file.getName().toLowerCase().endsWith("txt")) {
            searchFile(file.getAbsolutePath());
            return 1;
        }
        return 0;
    }

    private void searchFile(String filename) {
        new DocumentParser(filename).parse();
    }
}
