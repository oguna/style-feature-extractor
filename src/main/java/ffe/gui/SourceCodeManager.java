package ffe.gui;

import ffe.FeatureWriter;
import ffe.Token;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceFormatFeature;
import ffe.whitespace.WhiteSpaceVisitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;

public class SourceCodeManager {
    private final static String helloWorldContent = "public class HelloWorld {\n" +
            "    public static void main(String[] args){\n" +
            "        System.out.println(\"Hello, world.\");\n" +
            "    }\n" +
            "}";

    private Scanner scanner;
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
        try (FileWriter fw = new FileWriter(file);
             FeatureWriter featureWriter = new FeatureWriter(fw)) {
            for (WhiteSpaceFormatFeature feature : features) {
                featureWriter.write(feature);
            }
        }
    }

    public String getToken(Token token) {
        return text.getValue().substring(token.position, token.position + token.length);
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
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        char[] source = content.toCharArray();
        parser.setSource(source);
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        FeatureCollector collector = new FeatureCollector();
        List<WhiteSpaceVisitor> statementVisitors = WhiteSpaceVisitor.listStatementWhiteSpaceVisitor(source, collector);
        statementVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> expressionVisitors = WhiteSpaceVisitor.listExpressionWhiteSpaceVisitor(source, collector);
        expressionVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> arrayVisitors = WhiteSpaceVisitor.listArrayWhiteSpaceVisitor(source, collector);
        arrayVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> declarationVisitors = WhiteSpaceVisitor.listDeclarationWhiteSpaceVisitor(source, collector);
        declarationVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> parameterizedVisitors  = WhiteSpaceVisitor.listParameterizedWhiteSpaceVisitor(source, collector);
        parameterizedVisitors.forEach(unit::accept);
        List<WhiteSpaceFormatFeature> features = new ArrayList<>(collector.features);
        features.sort((a,b) -> a.token.position - b.token.position);
        this.scanner = getScanner(content);
        this.features.clear();
        this.features.addAll(features);
        this.text.setValue(content);
    }

    public void loadContent(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        initData(content);
    }
}
