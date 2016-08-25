package ffe.gui;

import ffe.Token;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;

public class SourceCodeManager {
    private String code;
    private Scanner scanner;

    public SourceCodeManager(String code) {
        this.code = code;
        this.scanner = getScanner(code);
    }

    public int calculateCodeAreaPosition(int position) {
        int line = scanner.getLineNumber(position);
        int column = position - scanner.getLineStart(line);
        return getStartPosition(line, code) + column;
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

    public String getToken(Token token) {
        return code.substring(token.position, token.position + token.length);
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
}
