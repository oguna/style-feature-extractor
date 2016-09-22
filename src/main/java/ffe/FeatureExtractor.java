package ffe;

import ffe.token.Token;
import ffe.token.TokenManager;
import ffe.whitespace.Direction;
import ffe.whitespace.SpacePreparator;
import ffe.whitespace.WhiteSpaceFormatFeature;
import ffe.whitespace.WhiteSpaceOption;
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
        List<Token> tokens = tokenizeSource(source);
        TokenManager manager = new TokenManager(tokens, content, DefaultCodeFormatterOptions.getDefaultSettings());
        SpacePreparator visitor = new SpacePreparator(manager);
        unit.accept(visitor);
        visitor.finishUp();
        // detect space
        for (Token token : tokens) {
            token.clearSpaceBefore();
            token.clearSpaceAfter();
            if (token.originalStart == 0) {
                token.spaceBefore();
            } else {
                if (Character.isWhitespace(source[token.originalStart - 1])) {
                    token.spaceBefore();
                }
            }
            if (token.originalEnd + 1 == source.length) {
                token.spaceAfter();
            } else {
                if (Character.isWhitespace(source[token.originalEnd + 1])) {
                    token.spaceAfter();
                }
            }
        }
        // collect feature values
        List<WhiteSpaceFormatFeature> features = new ArrayList<>();
        for (Token token : tokens) {
            if (token.afterFeature != null && !token.afterFeature.equals("FALSE") && !token.afterFeature.equals("TRUE")) {
                WhiteSpaceOption value = token.isSpaceAfter() ? WhiteSpaceOption.INSERT : WhiteSpaceOption.DO_NOT_INSERT;
                WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(token.afterFeature, value, token, Direction.AFTER);
                features.add(feature);
            }
            if (token.beforeFeature != null && !token.beforeFeature.equals("FALSE") && !token.beforeFeature.equals("TRUE")) {
                WhiteSpaceOption value = token.isSpaceBefore() ? WhiteSpaceOption.INSERT : WhiteSpaceOption.DO_NOT_INSERT;
                WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(token.beforeFeature, value, token, Direction.BEFORE);
                features.add(feature);
            }
        }
        return features;
    }

    private static List<Token> tokenizeSource(char[] sourceArray) {
        List<Token> tokens = new ArrayList<>();
        Scanner scanner = new Scanner(true, false, false/* nls */, 3407872L,
                null/* taskTags */, null/* taskPriorities */, false/* taskCaseSensitive */);
        scanner.setSource(sourceArray);
        while (true) {
            try {
                int tokenType = scanner.getNextToken();
                if (tokenType == TokenNameEOF)
                    break;
                Token token = Token.fromCurrent(scanner, tokenType);
                tokens.add(token);
            } catch (InvalidInputException e) {
                Token token = Token.fromCurrent(scanner, TokenNameNotAToken);
                tokens.add(token);
            }
        }
        return tokens;
    }
}
