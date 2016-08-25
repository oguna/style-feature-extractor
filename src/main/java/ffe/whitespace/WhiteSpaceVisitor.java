package ffe.whitespace;

import ffe.Token;
import ffe.whitespace.array.ArrayAllocationVisitor;
import ffe.whitespace.array.ArrayDeclarationVisitor;
import ffe.whitespace.array.ArrayElementAccessVisitor;
import ffe.whitespace.array.ArrayInitializerVisitor;
import ffe.whitespace.declaration.*;
import ffe.whitespace.expression.*;
import ffe.whitespace.parameterized.TypeArgumentsVisitor;
import ffe.whitespace.parameterized.TypeParametersVisitor;
import ffe.whitespace.parameterized.TypeReferenceVisitor;
import ffe.whitespace.parameterized.WildcardTypeVisitor;
import ffe.whitespace.statement.*;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameEOF;

public abstract class WhiteSpaceVisitor extends ASTVisitor {
    protected final char[] source;
    protected final FeatureCollector featureCollector;

    public WhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        this.source = source;
        this.featureCollector = featureCollector;
    }

    protected void collectFeature(String attribute, @NotNull Token token, Direction direction) {
        final WhiteSpaceFormatFeature feature;
        if (direction == Direction.BEFORE) {
            if (Character.isWhitespace(source[token.position - 1])) {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, direction);
            } else {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, direction);
            }
        } else if (direction == Direction.AFTER) {
            if (Character.isWhitespace(source[token.position + token.length])) {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, direction);
            } else {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, direction);
            }
        } else {
            throw new IllegalArgumentException();
        }
        featureCollector.collect(feature);
    }

    protected void collectFeature(String attribute, Token left, Token right) {
        Token token = new Token(left.position, right.position + right.length - left.position);
        if (Character.isWhitespace(source[left.position + left.length])) {
            WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, Direction.BETWEEN);
            featureCollector.collect(feature);
        } else {
            WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, Direction.BETWEEN);
            featureCollector.collect(feature);
        }
    }

    protected Token searchBackward(int targetToken, int offset) {
        try {
            int lastMatchTokenPosition = -1;
            String lastMatchTokenString = null;
            Scanner scanner = new Scanner();
            scanner.recordLineSeparator = true;
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.setSource(source);
            int tokenType;
            while ((tokenType = scanner.getNextToken()) != TokenNameEOF && offset >= scanner.getCurrentTokenStartPosition()) {
                int start = scanner.getCurrentTokenStartPosition();
                if (tokenType == targetToken) {
                    lastMatchTokenPosition = scanner.getCurrentTokenStartPosition();
                    lastMatchTokenString = scanner.getCurrentTokenString();
                }
            }
            if (lastMatchTokenPosition > 0) {
                return new Token(lastMatchTokenPosition, lastMatchTokenString.length());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Token searchForward(int targetToken, int offset) {
        try {
            Scanner scanner = new Scanner();
            scanner.recordLineSeparator = true;
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.setSource(source);
            int tokenType;
            while ((tokenType = scanner.getNextToken()) != TokenNameEOF) {
                int start = scanner.getCurrentTokenStartPosition();
                if (start < offset) {
                    continue;
                }
                if (tokenType == targetToken) {
                    return new Token(start, scanner.getCurrentTokenString().length());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Token searchForward(String targetToken, int offset) {
        try {
            Scanner scanner = new Scanner();
            scanner.recordLineSeparator = true;
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.setSource(source);
            int tokenType;
            while ((tokenType = scanner.getNextToken()) != TokenNameEOF) {
                int start = scanner.getCurrentTokenStartPosition();
                if (start < offset) {
                    continue;
                }
                if (Objects.equals(targetToken, scanner.getCurrentTokenString())) {
                    return new Token(start, scanner.getCurrentTokenString().length());
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpPositionForDebug(int pos) {
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

    public static List<WhiteSpaceVisitor> listStatementWhiteSpaceVisitor(char[] source, FeatureCollector collector) {
        return Arrays.asList(
                new AssertStatementWhiteSpaceVisitor(source, collector),
                new BlockStatementWhiteSpaceVisitor(source, collector),
                new CatchStatementWhiteSpaceVisitor(source, collector),
                new ForStatementWhiteSpaceVisitor(source, collector),
                new IfElseStatementWhiteSpaceVisitor(source, collector),
                new ReturnStatementWhiteSpaceVisitor(source, collector),
                new SwitchStatementWhiteSpaceVisitor(source, collector),
                new SynchronizedStatementWhiteSpaceVisitor(source, collector),
                new ThrowStatementWhiteSpaceVisitor(source, collector),
                new TryWithResourceStatementWhiteSpaceVisitor(source, collector),
                new WhileStatementWhiteSpaceVisitor(source, collector)
        );
    }

    public static List<WhiteSpaceVisitor> listExpressionWhiteSpaceVisitor(char[] source, FeatureCollector collector) {
        return Arrays.asList(
                new AssignExpressionWhiteSpaceVisitor(source, collector),
                new ConditionalExpressionWhiteSpaceVisitor(source, collector),
                new FunctionInvocationWhiteSpaceVisitor(source, collector),
                new OperatorWhiteSpaceVisitor(source, collector),
                new ParenthesizedExpressionWhiteSpaceVisitor(source, collector),
                new TypeCastWhiteSpaceVisitor(source, collector)
        );
    }

    public static List<WhiteSpaceVisitor> listArrayWhiteSpaceVisitor(char[] source, FeatureCollector collector) {
        return Arrays.asList(
                new ArrayElementAccessVisitor(source, collector),
                new ArrayAllocationVisitor(source, collector),
                new ArrayDeclarationVisitor(source, collector),
                new ArrayInitializerVisitor(source, collector)
        );
    }

    public static List<WhiteSpaceVisitor> listDeclarationWhiteSpaceVisitor(char[] source, FeatureCollector collector) {
        return Arrays.asList(
                new AnnotationTypeVisitor(source, collector),
                new AnnotationVisitor(source, collector),
                new ClassDeclarationVisitor(source, collector),
                new ConstructorVisitor(source, collector),
                new EnumVisitor(source, collector),
                new FieldDeclarationVisitor(source, collector),
                new LabelDeclarationVisitor(source, collector),
                new LambdaDeclarationVisitor(source, collector),
                new LocalVariablesDeclarationVisitor(source, collector),
                new MethodDeclarationVisitor(source, collector)
        );
    }

    public static List<WhiteSpaceVisitor> listParameterizedWhiteSpaceVisitor(char[] source, FeatureCollector collector) {
        return Arrays.asList(
                new TypeReferenceVisitor(source, collector),
                new TypeArgumentsVisitor(source, collector),
                new TypeParametersVisitor(source, collector),
                new WildcardTypeVisitor(source, collector)
        );
    }
}
