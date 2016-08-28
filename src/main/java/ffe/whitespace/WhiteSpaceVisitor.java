package ffe.whitespace;

import ffe.FeatureCollector;
import ffe.FormatFeatureVisitor;
import ffe.Token;
import ffe.TokenSequence;
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

public abstract class WhiteSpaceVisitor extends FormatFeatureVisitor {

    public WhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    protected void collectFeature(@NotNull String attribute, @NotNull Token token, @NotNull Direction direction) {
        final WhiteSpaceFormatFeature feature;
        if (direction == Direction.BEFORE) {
            if (tokenSequence.existWhiteSpaceBeforeToken(token)) {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, direction);
            } else {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, direction);
            }
        } else if (direction == Direction.AFTER) {
            if (tokenSequence.existWhiteSpaceAfterToken(token)) {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, direction);
            } else {
                feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, direction);
            }
        } else {
            throw new IllegalArgumentException();
        }
        featureCollector.collect(feature);
    }

    protected void collectFeature(@NotNull String attribute, @NotNull Token left, @NotNull Token right) {
        Token token = left;
        if (tokenSequence.existWhiteSpaceAfterToken(token)) {
            WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.INSERT, token, Direction.BETWEEN);
            featureCollector.collect(feature);
        } else {
            WhiteSpaceFormatFeature feature = new WhiteSpaceFormatFeature(attribute, WhiteSpaceOption.DO_NOT_INSERT, token, Direction.BETWEEN);
            featureCollector.collect(feature);
        }
    }

    public static List<WhiteSpaceVisitor> listWhiteSpaceVisitor(TokenSequence source, FeatureCollector collector) {
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
                new WhileStatementWhiteSpaceVisitor(source, collector),
                new AssignExpressionWhiteSpaceVisitor(source, collector),
                new ConditionalExpressionWhiteSpaceVisitor(source, collector),
                new FunctionInvocationWhiteSpaceVisitor(source, collector),
                new OperatorWhiteSpaceVisitor(source, collector),
                new ParenthesizedExpressionWhiteSpaceVisitor(source, collector),
                new TypeCastWhiteSpaceVisitor(source, collector),
                new ArrayElementAccessVisitor(source, collector),
                new ArrayAllocationVisitor(source, collector),
                new ArrayDeclarationVisitor(source, collector),
                new ArrayInitializerVisitor(source, collector),
                new AnnotationTypeVisitor(source, collector),
                new AnnotationVisitor(source, collector),
                new ClassDeclarationVisitor(source, collector),
                new ConstructorVisitor(source, collector),
                new EnumVisitor(source, collector),
                new FieldDeclarationVisitor(source, collector),
                new LabelDeclarationVisitor(source, collector),
                new LambdaDeclarationVisitor(source, collector),
                new LocalVariablesDeclarationVisitor(source, collector),
                new MethodDeclarationVisitor(source, collector),
                new TypeReferenceVisitor(source, collector),
                new TypeArgumentsVisitor(source, collector),
                new TypeParametersVisitor(source, collector),
                new WildcardTypeVisitor(source, collector)
        );
    }
}
