package service;

import model.DatabaseCollection;
import model.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class DocumentParser {
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final Mode MODE_NORMAL = new Mode(0, Mode.Type.NORMAL);
    public static final Pattern LEX_FORMAT = Pattern.compile("\\\\[a-zA-Z!]*");
    private String filename;

    public DocumentParser(String filename) {
        this.filename = filename;
    }

    public void parse() {
        List<Document> documentList = DatabaseCollection.getDocumentList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String title = "";
            Mode actualMode = new Mode(0, Mode.Type.NORMAL);
            StringBuilder content = new StringBuilder();
            StringBuilder wholeText = new StringBuilder();
            boolean titleMode = false;
            while (br.ready()) {
                String line = br.readLine().trim();
                wholeText.append(line).append(" ");
                actualMode = shouldBeIgnore(line, actualMode);
                if (actualMode.getType() != Mode.Type.NORMAL) {
                    continue;
                }
                if (line.equals(EMPTY)) {
                    continue;
                }

                if (line.startsWith("\\title{")) {
                    line = line.replace("\\title{", "");
                    titleMode = true;
                }
                if (titleMode) {
                    int end = line.indexOf("}");
                    int falseEnd = line.indexOf("\\}");
                    if (end > 0 && end != falseEnd - 1) {
                        title += line.substring(0, end);
                        titleMode = false;
                    } else {
                        title += line + " ";
                    }
                    continue;
                }
                line = removeUnnecessaryChars(line);
                content.append(line).append(SPACE);
            }
            String[] declaredKeywords = searchKeywords(wholeText.toString());
            documentList.add(new Document(filename,title, content.toString(), declaredKeywords));

        } catch (FileNotFoundException e) {
            System.out.println("No database available.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] searchKeywords(String text) {
        String str = "\\keywords{";
        int start = text.indexOf(str);
        if (start != -1) {
            text = text.substring(start + str.length());
            int stop = text.indexOf("}");
            text = text.substring(0, stop);
        } else {
            str = "\\begin{keyword}";
            start = text.indexOf(str);
            if (start == -1) {
                return null;
            }
            text = text.substring(start + str.length());
            int stop = text.indexOf("\\end{keyword}");
            text = text.substring(0, stop);
        }
        return text.split("[,;]");
    }

    private String removeUnnecessaryChars(String line) {
        line = LEX_FORMAT.matcher(line).replaceAll("");
        return line;
    }


    private Mode shouldBeIgnore(String line, Mode actualMode) {
        String ignored[] = {"%", "\\begin{table}", "\\begin{algo}", "\\begin{", "\\address{", "\\end{", "\\ead{", "\\documentclass{", "\\journal{", "\\author{", "\\bibitem{"};
        String closeTag[] = {"", "\\end{table}", "\\end{algo}", "}", "}", "}", "}", "}", "}", "}","\r\n"};
        for (int i = 0; i < ignored.length; i++) {
            boolean startsWith = line.contains(ignored[i]);
            boolean endsWith = line.contains(closeTag[i]);
            if (actualMode.getType() != Mode.Type.IGNORE_START) {
                if (startsWith) {
                    if (endsWith) {
                        return new Mode(i, Mode.Type.IGNORE_SINGLE);
                    }
                    return new Mode(i, Mode.Type.IGNORE_START);
                }
            } else {
                if (actualMode.getId() == i && endsWith) {
                    return new Mode(i, Mode.Type.IGNORE_STOP);
                }
            }
        }
        if (actualMode.getType() == Mode.Type.IGNORE_START) {
            return actualMode;
        }
        return MODE_NORMAL;

    }


}
