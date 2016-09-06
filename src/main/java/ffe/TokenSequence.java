package ffe;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;

public class TokenSequence {
    @NotNull
    private final Scanner scanner;
    @NotNull
    private final Token[] tokens;
    @NotNull
    private final char[] source;

    public TokenSequence(@NotNull String source) {
        this(source.toCharArray());
    }

    public boolean existWhiteSpaceBeforeToken(Token token) {
        return Character.isWhitespace(source[token.position - 1]);
    }

    public boolean existWhiteSpaceAfterToken(Token token) {
        int pos = token.position + token.length;
        return pos >= source.length || Character.isWhitespace(source[token.position + token.length]);
    }

    public TokenSequence(@NotNull char[] source) {
        this.source = source;
        List<Token> tokenList = new ArrayList<>();
        try {
            scanner = new Scanner();
            scanner.recordLineSeparator = true;
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.tokenizeComments = true;
            scanner.setSource(source);
            int tokenType;
            while ((tokenType = scanner.getNextToken()) != TokenNameEOF) {
                int start = scanner.getCurrentTokenStartPosition();
                int length = scanner.getCurrentTokenEndPosition() - start + 1;
                int line = scanner.getLineNumber(start);
                Token token = new Token(tokenType, start, length, line);
                tokenList.add(token);
            }
            this.tokens = tokenList.toArray(new Token[tokenList.size()]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public Token searchForwardInNode(int tokenType, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() > tokens[startIndex].position) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (node.getStartPosition() + node.getLength() > tokens[endIndex].position + tokens[endIndex].length) {
            endIndex++;
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (tokenType == tokens[i].tokenType) {
                return tokens[i];
            }
        }
        return null;
    }

    @Nullable
    public Token searchForwardInNode(Collection<Integer> tokenTypes, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() > tokens[startIndex].position) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (node.getStartPosition() + node.getLength() > tokens[endIndex].position + tokens[endIndex].length) {
            endIndex++;
        }
        for (int i = startIndex; i <= endIndex; i++) {
            for (int tokenType : tokenTypes) {
                if (tokenType == tokens[i].tokenType) {
                    return tokens[i];
                }
            }
        }
        return null;
    }

    @Nullable
    public Token searchBackwardInNode(int tokenType, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() > tokens[startIndex].position) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (node.getStartPosition() + node.getLength() > tokens[endIndex].position + tokens[endIndex].length) {
            endIndex++;
        }
        for (int i = endIndex; i >= startIndex; i--) {
            if (tokenType == tokens[i].tokenType) {
                return tokens[i];
            }
        }
        return null;
    }

    @Nullable
    public Token searchBackwardBeforeNode(int tokenType, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() > tokens[startIndex].position) {
            startIndex++;
        }
        for (int i = startIndex - 1; i >= 0; i--) {
            if (tokenType == tokens[i].tokenType) {
                return tokens[i];
            }
        }
        return null;

    }

    @Nullable
    public Token searchForwardAfterNode(int tokenType, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() + node.getLength() > tokens[startIndex].position + tokens[startIndex].length) {
            startIndex++;
        }
        for (int i = startIndex; i < tokens.length; i++) {
            if (tokenType == tokens[i].tokenType) {
                return tokens[i];
            }
        }
        return null;
    }

    @Nullable
    public Token searchForwardAfterNode(Collection<Integer> tokenTypes, @NotNull ASTNode node) {
        int startIndex = 0;
        while (node.getStartPosition() + node.getLength() > tokens[startIndex].position + tokens[startIndex].length) {
            startIndex++;
        }
        for (int i = startIndex; i < tokens.length; i++) {
            for (int tokenType : tokenTypes) {
                if (tokenType == tokens[i].tokenType) {
                    return tokens[i];
                }
            }
        }
        return null;
    }

    public static int getTokenType(String token) {
        try {
            Scanner scanner = new Scanner();
            scanner.recordLineSeparator = true;
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.tokenizeComments = true;
            scanner.setSource(token.toCharArray());
            int tokenType = scanner.getNextToken();
            if (tokenType == TokenNameEOF) {
                throw new RuntimeException();
            } else {
                return tokenType;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dumpPositionForDebug(int pos) {
        int lineStartPos;
        for (lineStartPos = pos; lineStartPos  >= 0; lineStartPos--) {
            if (source[lineStartPos] == '\n' || source[lineStartPos] == '\r') {
                lineStartPos++;
                break;
            }
        }
        int lineEndPos;
        for (lineEndPos = pos; lineEndPos  < source.length; lineEndPos++) {
            if (source[lineEndPos] == '\n' || source[lineEndPos] == '\r') {
                lineEndPos--;
                break;
            }
        }
        String line = new String(source, lineStartPos, lineEndPos - lineStartPos + 1);
        StringBuilder sb = new StringBuilder(pos - lineStartPos);
        for (int i = 0; i < (pos - lineStartPos); i++) {
            sb.append(' ');
        }
        sb.append('^');
        System.out.println(line);
        System.out.println(sb.toString());
    }
}
