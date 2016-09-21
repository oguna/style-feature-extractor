package ffe.gui;

import ffe.FeatureWriter;
import ffe.Token;
import ffe.FeatureCollector;
import ffe.TokenSequence;
import ffe.token.TokenManager;
import ffe.whitespace.SpacePreparator;
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
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameNotAToken;

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
        List<ffe.token.Token> tokens = tokenizeSource(source);
        TokenManager manager = new TokenManager(tokens, content, DefaultCodeFormatterOptions.getDefaultSettings());
        SpacePreparator visitor = new SpacePreparator(manager);
        unit.accept(visitor);
        List<WhiteSpaceFormatFeature> features = new ArrayList<>(visitor.features);
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

    private static List<ffe.token.Token> tokenizeSource(char[] sourceArray) {
        List<ffe.token.Token> tokens = new ArrayList<>();
        Scanner scanner = new Scanner(true, false, false/* nls */, 3407872L,
                null/* taskTags */, null/* taskPriorities */, false/* taskCaseSensitive */);
        scanner.setSource(sourceArray);
        while (true) {
            try {
                int tokenType = scanner.getNextToken();
                if (tokenType == TokenNameEOF)
                    break;
                ffe.token.Token token = ffe.token.Token.fromCurrent(scanner, tokenType);
                tokens.add(token);
            } catch (InvalidInputException e) {
                ffe.token.Token token = ffe.token.Token.fromCurrent(scanner, TokenNameNotAToken);
                tokens.add(token);
            }
        }
        // トークンに前後の空白情報を付与する
        for (ffe.token.Token token : tokens) {
            if (token.originalStart == 0) {
                token.spaceBefore();
            } else {
                if (Character.isSpaceChar(sourceArray[token.originalStart - 1])) {
                    token.spaceBefore();
                }
            }
            if (token.originalEnd == sourceArray.length) {
                token.spaceAfter();
            } else {
                if (Character.isSpaceChar(sourceArray[token.originalEnd])) {
                    token.spaceAfter();
                }
            }
        }
        return tokens;
    }
}
