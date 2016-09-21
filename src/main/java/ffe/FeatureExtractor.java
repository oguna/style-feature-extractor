package ffe;

import ffe.token.TokenManager;
import ffe.whitespace.SpacePreparator;
import ffe.whitespace.WhiteSpaceFormatFeature;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameNotAToken;

public class FeatureExtractor {
    public static List<WhiteSpaceFormatFeature> extract(String content) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        char[] source = content.toCharArray();
        parser.setSource(source);
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        List<ffe.token.Token> tokens = tokenizeSource(source);
        TokenManager manager = new TokenManager(tokens, content, DefaultCodeFormatterOptions.getDefaultSettings());
        SpacePreparator visitor = new SpacePreparator(manager);
        unit.accept(visitor);
        return visitor.features;
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
