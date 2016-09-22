package ffe.gui;

import ffe.FeatureExtractor;
import ffe.output.CsvWriter;
import ffe.output.IFeatureWriter;
import ffe.token.Token;
import ffe.whitespace.WhiteSpaceFormatFeature;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;

public class SourceCodeManager {
    private final static String helloWorldContent = "public class HelloWorld {\n" +
            "    public static void main(String[] args){\n" +
            "        System.out.println(\"Hello, world.\");\n" +
            "    }\n" +
            "}";

    private Scanner scanner;
    private String filename = null;
    public final ObservableList<WhiteSpaceFormatFeature> features;
    public final StringProperty text = new SimpleStringProperty();

    public SourceCodeManager() {
        features = FXCollections.observableArrayList();
        setSourceCode(helloWorldContent);
    }

    public void setSourceCode(String content) {
        this.text.set(content);
        initData(content);
        this.scanner = getScanner(content);
    }

    public int calculateCodeAreaPosition(int position) {
        int line = scanner.getLineNumber(position);
        int column = position - scanner.getLineStart(line);
        return getStartPosition(line, text.getValue()) + column;
    }

    public String getPositionDescription(int position) {
        int line = scanner.getLineNumber(position);
        int column = position - scanner.getLineStart(line);
        return position + "<" + line + ":" + column + ">";
    }

    private static Scanner getScanner(String content) {
        try {
            Scanner scanner = new Scanner();
            scanner.setSource(content.toCharArray());
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.tokenizeComments = true;
            scanner.tokenizeWhiteSpace = true;
            scanner.recordLineSeparator = true;
            while (scanner.getNextToken() != TokenNameEOF) {
            }
            return scanner;
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFeatures(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            IFeatureWriter featureWriter = new CsvWriter(fw);
            for (WhiteSpaceFormatFeature feature : features) {
                featureWriter.write(feature, filename ,this.text.getValue());
            }
        }
    }

    public String getToken(Token token) {
        return text.getValue().substring(token.originalStart, token.originalEnd + 1);
    }

    private static int getStartPosition(int line, String text) {
        text = text.replace("\r", "");
        int currentLine = 1;
        for (int i = 0; i < text.length(); i++) {
            if (currentLine == line) {
                return i;
            }
            if (text.charAt(i) == '\n') {
                currentLine++;
            }
        }
        return -1;
    }

    public void initData(String content) {
        List<WhiteSpaceFormatFeature> features = FeatureExtractor.extract(content);
        features.sort((a,b) -> a.token.originalStart - b.token.originalStart);
        this.scanner = getScanner(content);
        this.features.clear();
        this.features.addAll(features);
        this.text.setValue(content);
    }

    public void loadContent(File file) throws IOException {
        this.filename = file.toString();
        String content = new String(Files.readAllBytes(file.toPath()));
        initData(content);
    }
    public void loadContent(String content) {
        this.filename = null;
        initData(content);
    }
}
