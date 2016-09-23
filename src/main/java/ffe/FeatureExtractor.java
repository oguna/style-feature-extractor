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
            // トークンの前後に空白が存在するか確認
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
            // トークンの前後に別のトークンが存在するか確認
            resolveLineBreak(token, content);
        }
        // collect feature values
        List<WhiteSpaceFormatFeature> features = new ArrayList<>();
        for (int i = 0; i < tokens.size() - 1; i++) {
            Token a = tokens.get(i);
            Token b = tokens.get(i + 1);
            resolveContinuousTokens(a, b, features);
            /*if (token.beforeFeature != null && !token.beforeFeature.equals("false") && !token.beforeFeature.equals("true")) {
                WhiteSpaceOption value = token.isSpaceBefore() ? WhiteSpaceOption.INSERT : WhiteSpaceOption.DO_NOT_INSERT;
                WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(token.beforeFeature, value, token, Direction.BEFORE);
                if (i < tokens.size() - 1 && token.isSpaceBefore()
                        && tokens.get(i+1).beforeFeature != null
                        && !tokens.get(i+1).beforeFeature.equals("false")
                        && !tokens.get(i+1).beforeFeature.equals("true")
                        && tokens.get(i+1).isSpaceBefore()) {
                    // この直後のトークンも考慮した特徴を生成する
                    String name = token.afterFeature + " | " + tokens.get(i + 1).beforeFeature.substring(31);
                    feature = new WhiteSpaceFormatFeature(name, WhiteSpaceOption.INSERT, token, Direction.AFTER);
                    tokens.get(i + 1).spaceBefore("false");
                }
                features.add(feature);
            }
            if (token.afterFeature != null && !token.afterFeature.equals("false") && !token.afterFeature.equals("true")) {
                WhiteSpaceOption value = token.isSpaceAfter() ? WhiteSpaceOption.INSERT : WhiteSpaceOption.DO_NOT_INSERT;
                WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(token.afterFeature, value, token, Direction.AFTER);
                features.add(feature);
            }*/

        }
        // 検出した特徴の中に改行を空白としているものがあれば削除
        for (int i = 0; i < features.size(); i++) {
            WhiteSpaceFormatFeature feature = features.get(i);
            if (feature.direction == Direction.BEFORE && feature.token.getLineBreaksBefore() == 1) {
                features.remove(i);
                i--;
            } else if (feature.direction == Direction.AFTER && feature.token.getLineBreaksAfter() == 1) {
                features.remove(i);
                i--;
            }
        }
        return features;
    }

    private static void resolveLineBreak(Token token, String content) {
        int start = token.originalStart - 1;
        while (start > 0 && Character.isWhitespace(content.charAt(start)) && content.charAt(start) != '\n' && content.charAt(start) != '\r') {
            start--;
        }
        if (start < 0 || content.charAt(start) == '\n' || content.charAt(start) == '\r') {
            token.putLineBreaksBefore(1);
        }
        int end = token.originalEnd + 1;
        while (end < content.length() - 1 && Character.isWhitespace(content.charAt(end)) && content.charAt(end) != '\n' && content.charAt(end) != '\r') {
            end++;
        }
        if (end > content.length() - 1 || content.charAt(end) == '\n' || content.charAt(end) == '\r') {
            token.putLineBreaksAfter(1);
        }
    }

    private static void resolveContinuousTokens(Token a, Token b, List<WhiteSpaceFormatFeature> features) {
        assert a.originalStart < b.originalStart;
        if (!a.isSpaceAfter() && !b.isSpaceBefore()) {
            // トークン間に空白が存在しない場合
            if (!a.afterFeature.equals("true") && !a.afterFeature.equals("false")) {
                features.add(new WhiteSpaceFormatFeature(a.afterFeature, WhiteSpaceOption.DO_NOT_INSERT, a, Direction.AFTER));
            }
            if (!b.beforeFeature.equals("true") && !b.beforeFeature.equals("false")) {
                features.add(new WhiteSpaceFormatFeature(b.beforeFeature, WhiteSpaceOption.DO_NOT_INSERT, b, Direction.BEFORE));
            }
        } else {
            // トークン間に空白が存在する場合
            if (a.afterFeature.equals("true") || b.beforeFeature.equals("true")) {
                // どちらかがtrueなら必ず空白が挿入されるので
                // フォーマットの特徴として扱わない
            } else if (!a.afterFeature.equals("false") && !a.afterFeature.equals("true") &&
                    !b.beforeFeature.equals("false") && !b.beforeFeature.equals("true")) {
                // どちらも特徴を持つなら、結合した特徴を定義する
                String featureName = a.afterFeature + " | " + b.beforeFeature.substring(31);
                features.add(new WhiteSpaceFormatFeature(featureName, WhiteSpaceOption.INSERT, a, Direction.AFTER));
            } else if (!a.afterFeature.equals("false") && !a.afterFeature.equals("true") && b.afterFeature.equals("false")) {
                features.add(new WhiteSpaceFormatFeature(a.afterFeature, WhiteSpaceOption.INSERT, a, Direction.AFTER));
            } else if (!b.beforeFeature.equals("false") && !b.beforeFeature.equals("true") && a.afterFeature.equals("false")) {
                features.add(new WhiteSpaceFormatFeature(b.beforeFeature, WhiteSpaceOption.INSERT, b, Direction.BEFORE));
            }
        }
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
