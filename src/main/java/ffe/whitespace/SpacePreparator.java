/*******************************************************************************
 * Copyright (c) 2014, 2016 Mateusz Matela and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] Formatter does not format Java code correctly, especially when max line width is set - https://bugs.eclipse.org/303519
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] IndexOutOfBoundsException in TokenManager - https://bugs.eclipse.org/462945
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] follow up bug for comments - https://bugs.eclipse.org/458208
 *******************************************************************************/
package ffe.whitespace;

import ffe.token.Token;
import ffe.token.TokenManager;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class SpacePreparator extends ASTVisitor {
    TokenManager tm;
    @NotNull
    public final List<WhiteSpaceFormatFeature> features;

    public SpacePreparator(TokenManager tokenManager) {
        this.tm = tokenManager;
        this.features = new ArrayList<>();
    }

    private void addFeature(String format, Direction direction, Token token) {
        boolean space = direction == Direction.BEFORE ? token.isSpaceBefore() : token.isSpaceAfter();
        WhiteSpaceOption option = space ? WhiteSpaceOption.INSERT : WhiteSpaceOption.DO_NOT_INSERT;
        features.add(new WhiteSpaceFormatFeature(format, option, token));
    }

    @Override
    public boolean preVisit2(ASTNode node) {
        boolean isMalformed = (node.getFlags() & ASTNode.MALFORMED) != 0;
        return !isMalformed;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        handleSemicolon(node);
        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        handleSemicolon(node);
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.getName().getStartPosition() == -1)
            return true; // this is a fake type created by parsing in class body mode

        handleToken(node.getName(), TokenNameIdentifier, null, null);

        List<TypeParameter> typeParameters = node.typeParameters();
        handleTypeParameters(typeParameters);

        if (!node.isInterface() && !node.superInterfaceTypes().isEmpty()) {
            // fix for: class A<E> extends ArrayList<String>implements Callable<String>
            handleToken(node.getName(), TokenNameimplements, null, null);
        }

        handleToken(node.getName(), TokenNameLBRACE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION, null);
        handleCommas(node.superInterfaceTypes(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES);
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        handleToken(node.getName(), TokenNameLBRACE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_DECLARATION, null);
        handleCommas(node.superInterfaceTypes(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES);
        handleCommas(node.enumConstants(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_DECLARATIONS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_DECLARATIONS);
        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        List<Expression> arguments = node.arguments();
        Token openingParen = null;
        if (!arguments.isEmpty()) {
            openingParen = this.tm.firstTokenIn(node, TokenNameLPAREN);
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT, Direction.AFTER, openingParen);
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT, Direction.AFTER, openingParen);
            handleTokenAfter(arguments.get(arguments.size() - 1), TokenNameRPAREN,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ENUM_CONSTANT, null);
        } else {
            // look for empty parenthesis, may not be there
            int from = this.tm.firstIndexIn(node.getName(), TokenNameIdentifier) + 1;
            AnonymousClassDeclaration classDeclaration = node.getAnonymousClassDeclaration();
            int to = classDeclaration != null ? this.tm.firstIndexBefore(classDeclaration, -1)
                    : this.tm.lastIndexIn(node, -1);
            for (int i = from; i <= to; i++) {
                if (this.tm.get(i).tokenType == TokenNameLPAREN) {
                    openingParen = this.tm.get(i);
                    addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ENUM_CONSTANT,Direction.AFTER, openingParen);
                    break;
                }
            }
        }
        if (openingParen != null) {
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ENUM_CONSTANT, Direction.BEFORE, openingParen);
        }
        handleCommas(arguments, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_CONSTANT_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_CONSTANT_ARGUMENTS);
        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        String spaceBeforeOpenBrace = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANONYMOUS_TYPE_DECLARATION;
        if (node.getParent() instanceof EnumConstantDeclaration)
            spaceBeforeOpenBrace = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_CONSTANT;
        handleToken(node, TokenNameLBRACE, spaceBeforeOpenBrace, null);
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        handleToken(node.getName(), TokenNameIdentifier, null, null);

        String spaceBeforeOpenParen = node.isConstructor()
                ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION
                : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION;
        String spaceAfterOpenParen = node.isConstructor()
                ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION
                : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION;
        String spaceBetweenEmptyParens = node.isConstructor()
                ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION
                : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION;
        if (handleEmptyParens(node.getName(), spaceBetweenEmptyParens)) {
            handleToken(node.getName(), TokenNameLPAREN, spaceBeforeOpenParen, null);
        } else {
            handleToken(node.getName(), TokenNameLPAREN, spaceBeforeOpenParen, spaceAfterOpenParen);

            String spaceBeforeCloseParen = node.isConstructor()
                    ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION
                    : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION;
            List<SingleVariableDeclaration> params = node.parameters();
            ASTNode beforeBrace = params.isEmpty() ? node.getName() : params.get(params.size() - 1);
            handleTokenAfter(beforeBrace, TokenNameRPAREN, spaceBeforeCloseParen, null);
        }

        if (node.getBody() != null) {
            String featureName = node.isConstructor() ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_CONSTRUCTOR_DECLARATION
                    : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_METHOD_DECLARATION;
            addFeature(featureName, Direction.BEFORE, this.tm.firstTokenIn(node.getBody(), TokenNameLBRACE));
        }

        String beforeComma = node.isConstructor()
                ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS
                : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS;
        String afterComma = node.isConstructor()
                ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS
                : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS;
        handleCommas(node.parameters(), beforeComma, afterComma);

        List<Type> thrownExceptionTypes = node.thrownExceptionTypes();
        if (!thrownExceptionTypes.isEmpty()) {
            this.tm.firstTokenBefore(thrownExceptionTypes.get(0), TokenNamethrows).spaceBefore();

            beforeComma = node.isConstructor()
                    ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS
                    : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_THROWS;
            afterComma = node.isConstructor()
                    ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS
                    : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS;
            handleCommas(thrownExceptionTypes, beforeComma, afterComma);
        }

        List<TypeParameter> typeParameters = node.typeParameters();
        if (!typeParameters.isEmpty()) {
            handleTypeParameters(typeParameters);
            handleTokenBefore(typeParameters.get(0), TokenNameLESS, null, null);
            handleTokenAfter(typeParameters.get(typeParameters.size() - 1), TokenNameGREATER, null, null);
        }

        handleSemicolon(node);
        return true;
    }

    private void handleTypeParameters(List<TypeParameter> typeParameters) {
        if (!typeParameters.isEmpty()) {
            handleTokenBefore(typeParameters.get(0), TokenNameLESS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS);
            handleTokenAfter(typeParameters.get(typeParameters.size() - 1), TokenNameGREATER,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS);
            handleCommas(typeParameters, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_PARAMETERS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_PARAMETERS);
        }
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        handleToken((ASTNode) node.fragments().get(0), TokenNameIdentifier, null, null);
        handleCommas(node.fragments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS);
        handleSemicolon(node);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        handleToken((ASTNode) node.fragments().get(0), TokenNameIdentifier, null, null);
        handleCommas(node.fragments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        if (node.getInitializer() != null) {
            handleToken(node.getName(), TokenNameEQUAL, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR);
        }
        return true;
    }

    @Override
    public void endVisit(SingleVariableDeclaration node) {
        // this must be endVisit in case a space added by a visit on a child node needs to be cleared
        if (node.isVarargs()) {
            handleTokenBefore(node.getName(), TokenNameELLIPSIS, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ELLIPSIS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ELLIPSIS);
            List<Annotation> varargsAnnotations = node.varargsAnnotations();
            if (!varargsAnnotations.isEmpty()) {
                //this.tm.firstTokenIn(varargsAnnotations.get(0), TokenNameAT).spaceBefore();
                //this.tm.lastTokenIn(varargsAnnotations.get(varargsAnnotations.size() - 1), -1).clearSpaceAfter();
            }
        } else {
            handleToken(node.getName(), TokenNameIdentifier, null, null);
        }
    }

    @Override
    public boolean visit(SwitchStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH);
        handleTokenAfter(node.getExpression(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH, null);
        handleTokenAfter(node.getExpression(), TokenNameLBRACE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH, null);
        handleSemicolon(node.statements());
        return true;
    }

    @Override
    public boolean visit(SwitchCase node) {
        if (node.isDefault()) {
            handleToken(node, TokenNameCOLON, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_DEFAULT, null);
        } else {
            handleToken(node, TokenNamecase, null, null);
            handleToken(node.getExpression(), TokenNameCOLON, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE, null);
        }
        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        handleTokenBefore(node.getExpression(), TokenNameLPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE);
        handleTokenBefore(node.getExpression(), TokenNamewhile,
                !(node.getBody() instanceof Block) ? null : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, null);
        handleTokenAfter(node.getExpression(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, null);
        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE);
        handleTokenBefore(node.getBody(), TokenNameRPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE,
                null);
        return true;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SYNCHRONIZED);
        handleTokenBefore(node.getBody(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SYNCHRONIZED, null);
        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
        List<VariableDeclarationExpression> resources = node.resources();
        if (!resources.isEmpty()) {
            handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRY,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TRY);
            handleTokenBefore(node.getBody(), TokenNameRPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TRY,
                    null);
            for (int i = 1; i < resources.size(); i++) {
                handleTokenBefore(resources.get(i), TokenNameSEMICOLON,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_TRY_RESOURCES,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_TRY_RESOURCES);
            }
            // there can be a semicolon after the last resource
            int index = this.tm.firstIndexAfter(resources.get(resources.size() - 1), -1);
            while (index < this.tm.size()) {
                Token token = this.tm.get(index++);
                if (token.tokenType == TokenNameSEMICOLON) {
                    handleToken(token, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_TRY_RESOURCES, null);
                } else if (token.tokenType == TokenNameRPAREN) {
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean visit(CatchClause node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH);
        handleTokenBefore(node.getBody(), TokenNameRPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH,
                null);
        return true;
    }

    @Override
    public boolean visit(AssertStatement node) {
        //this.tm.firstTokenIn(node, TokenNameassert).spaceAfter();
        if (node.getMessage() != null) {
            handleTokenBefore(node.getMessage(), TokenNameCOLON, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ASSERT,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ASSERT);
        }
        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        if (node.getExpression() != null) {
            int returnTokenIndex = this.tm.firstIndexIn(node, TokenNamereturn);
            if ((node.getExpression() instanceof ParenthesizedExpression)) {
                addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_RETURN, Direction.AFTER, this.tm.get(returnTokenIndex));
            }
        }
        return true;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        int returnTokenIndex = this.tm.firstIndexIn(node, TokenNamethrow);
        if (this.tm.get(returnTokenIndex + 1).tokenType == TokenNameLPAREN) {
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_THROW, Direction.AFTER, this.tm.get(returnTokenIndex));
        }
        return true;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        handleToken(node, TokenNameCOLON, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_LABELED_STATEMENT,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_LABELED_STATEMENT);
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        handleToken(node, TokenNameAT, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_AT_IN_ANNOTATION_TYPE_DECLARATION,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION_TYPE_DECLARATION);
        handleToken(node.getName(), TokenNameLBRACE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ANNOTATION_TYPE_DECLARATION, null);
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        handleToken(node.getName(), TokenNameIdentifier, null, null);
        handleToken(node.getName(), TokenNameLPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION_TYPE_MEMBER_DECLARATION, null);
        handleEmptyParens(node.getName(),
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ANNOTATION_TYPE_MEMBER_DECLARATION);
        if (node.getDefault() != null)
            handleTokenBefore(node.getDefault(), TokenNamedefault, null, null);
        return true;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        handleAnnotation(node, true);
        handleCommas(node.values(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ANNOTATION,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ANNOTATION);
        return true;
    }

    @Override
    public boolean visit(MemberValuePair node) {
        handleToken(node, TokenNameEQUAL, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR);
        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        handleAnnotation(node, true);
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        handleAnnotation(node, false);
        return true;
    }

    private void handleAnnotation(Annotation node, boolean handleParenthesis) {
        handleToken(node, TokenNameAT, null, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION);
        if (handleParenthesis) {
            handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ANNOTATION);
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ANNOTATION, Direction.BEFORE, this.tm.lastTokenIn(node, TokenNameRPAREN));
        }

        ASTNode parent = node.getParent();
        boolean skipSpaceAfter = parent instanceof Annotation || parent instanceof MemberValuePair
                || (parent instanceof AnnotationTypeMemberDeclaration
                && ((AnnotationTypeMemberDeclaration) parent).getDefault() == node)
                || parent instanceof ArrayInitializer;
        if (!skipSpaceAfter)
            this.tm.lastTokenIn(node, -1).spaceAfter();
    }

    @Override
    public boolean visit(LambdaExpression node) {
        handleToken(node, TokenNameARROW, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_LAMBDA_ARROW,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_LAMBDA_ARROW);
        List<VariableDeclaration> parameters = node.parameters();
        if (node.hasParentheses()) {
            if (handleEmptyParens(node, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION)) {
                handleToken(node, TokenNameLPAREN,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION, null);
            } else {
                handleToken(node, TokenNameLPAREN,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION);

                handleTokenBefore(node.getBody(), TokenNameRPAREN,
                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION, null);
            }
            handleCommas(parameters, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS);
        }
        return true;
    }

    @Override
    public boolean visit(Block node) {
        handleSemicolon(node.statements());

        ASTNode parent = node.getParent();
        if (parent.getLength() == 0)
            return true; // this is a fake block created by parsing in statements mode
        if (parent instanceof MethodDeclaration)
            return true; // spaces handled in #visit(MethodDeclaration)

        handleToken(node, TokenNameLBRACE, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK, null);
        if (parent instanceof Statement || parent instanceof CatchClause) {
            int closeBraceIndex = this.tm.lastIndexIn(node, TokenNameRBRACE);
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, Direction.AFTER, this.tm.get(closeBraceIndex));
        }
        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF);

        Statement thenStatement = node.getThenStatement();
        int closingParenIndex = this.tm.firstIndexBefore(thenStatement, TokenNameRPAREN);
        handleToken(this.tm.get(closingParenIndex), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF,
				/* space before then statement may be needed if it will stay on the same line */
                null);

        if (thenStatement instanceof Block && this.tm.isGuardClause((Block) thenStatement)) {
            handleToken(thenStatement, TokenNameLBRACE, null, null);
            this.tm.lastTokenIn(node, TokenNameRBRACE).spaceBefore();
        }

        handleSemicolon(thenStatement);
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR);
        handleTokenBefore(node.getBody(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, null);
        handleCommas(node.initializers(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS);
        handleCommas(node.updaters(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INCREMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INCREMENTS);

        boolean part1Empty = node.initializers().isEmpty();
        boolean part2Empty = node.getExpression() == null;
        boolean part3Empty = node.updaters().isEmpty();
        handleToken(node, TokenNameSEMICOLON, !part1Empty ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR : null,
                !part2Empty ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR : null);
        handleTokenBefore(node.getBody(), TokenNameSEMICOLON,
                !part2Empty ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR : null,
                !part3Empty ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR : null);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        ASTNode parent = node.getParent();
        if (parent instanceof ForStatement) {
            handleCommas(node.fragments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS);
        } else if (parent instanceof ExpressionStatement) {
            handleCommas(node.fragments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS);
        }
        this.tm.firstTokenAfter(node.getType(), -1).spaceBefore();
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        handleToken(node, TokenNameLPAREN, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR);
        handleTokenBefore(node.getBody(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, null);
        handleTokenAfter(node.getParameter(), TokenNameCOLON, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR);
        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        handleTypeArguments(node.typeArguments());
        handleInvocation(node, node.getName());
        handleCommas(node.arguments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS);
        return true;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        handleTypeArguments(node.typeArguments());
        handleInvocation(node, node.getName());
        handleCommas(node.arguments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS);
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        List<Type> typeArguments = node.typeArguments();
        handleTypeArguments(typeArguments);
        handleInvocation(node, node.getType(), node.getAnonymousClassDeclaration());
        if (!typeArguments.isEmpty()) {
            handleTokenBefore(typeArguments.get(0), TokenNamenew, null, null); // fix for: new<Integer>A<String>()
        }
        handleCommas(node.arguments(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ALLOCATION_EXPRESSION,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ALLOCATION_EXPRESSION);
        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        handleTypeArguments(node.typeArguments());
        handleInvocation(node, node);
        handleCommas(node.arguments(),
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS);
        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        handleTypeArguments(node.typeArguments());
        handleInvocation(node, node);
        handleCommas(node.arguments(),
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS);
        return true;
    }

    private void handleInvocation(ASTNode invocationNode, ASTNode nodeBeforeOpeningParen) {
        handleInvocation(invocationNode, nodeBeforeOpeningParen, null);
    }

    private void handleInvocation(ASTNode invocationNode, ASTNode nodeBeforeOpeningParen,
                                  ASTNode nodeAfterClosingParen) {
        if (handleEmptyParens(nodeBeforeOpeningParen,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION)) {
            handleToken(nodeBeforeOpeningParen, TokenNameLPAREN,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, null);
        } else {
            handleToken(nodeBeforeOpeningParen, TokenNameLPAREN,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION);
            Token closingParen = nodeAfterClosingParen == null
                    ? this.tm.lastTokenIn(invocationNode, TokenNameRPAREN)
                    : this.tm.firstTokenBefore(nodeAfterClosingParen, TokenNameRPAREN);
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, Direction.BEFORE, closingParen);
        }
    }

    @Override
    public boolean visit(Assignment node) {
        handleOperator(node.getOperator().toString(), node.getRightHandSide(),
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR);
        return true;
    }

    @Override
    public boolean visit(InfixExpression node) {
        String operator = node.getOperator().toString();
        handleOperator(operator, node.getRightOperand(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR);
        List<Expression> extendedOperands = node.extendedOperands();
        for (Expression operand : extendedOperands) {
            handleOperator(operator, operand, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR);
        }
        return true;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        Operator operator = node.getOperator();
        if (operator.equals(Operator.INCREMENT) || operator.equals(Operator.DECREMENT)) {
            handleOperator(operator.toString(), node.getOperand(),
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR);
        } else {
            handleOperator(operator.toString(), node.getOperand(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR);
        }
        return true;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        String operator = node.getOperator().toString();
        int i = this.tm.firstIndexAfter(node.getOperand(), -1);
        while (!operator.equals(this.tm.toString(i))) {
            i++;
        }
        handleToken(this.tm.get(i), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR);
        return true;
    }

    private void handleOperator(String operator, ASTNode nodeAfter, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            int i = this.tm.firstIndexBefore(nodeAfter, -1);
            while (!operator.equals(this.tm.toString(i))) {
                i--;
            }
            handleToken(this.tm.get(i), spaceBefore, spaceAfter);
        }
    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        handleToken(node, TokenNameLPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSION);
        handleTokenAfter(node.getExpression(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PARENTHESIZED_EXPRESSION, null);
        return true;
    }

    @Override
    public boolean visit(CastExpression node) {
        handleToken(node, TokenNameLPAREN, null, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CAST);
        handleTokenBefore(node.getExpression(), TokenNameRPAREN,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CAST,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CAST);
        return true;
    }

    @Override
    public boolean visit(IntersectionType node) {
        List<Type> types = node.types();
        for (int i = 1; i < types.size(); i++)
            handleTokenBefore(types.get(i), TokenNameAND, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR);
        return true;
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        handleTokenBefore(node.getThenExpression(), TokenNameQUESTION,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_CONDITIONAL,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_CONDITIONAL);
        handleTokenBefore(node.getElseExpression(), TokenNameCOLON,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL);
        return true;
    }

    @Override
    public boolean visit(ArrayType node) {
        ASTNode parent = node.getParent();
        String spaceBeofreOpening, spaceBetween;
        if (parent instanceof ArrayCreation) {
            spaceBeofreOpening = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION;
            spaceBetween = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_ARRAY_ALLOCATION_EXPRESSION;
        } else {
            spaceBeofreOpening = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE;
            spaceBetween = DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE;
        }
        List<Dimension> dimensions = node.dimensions();
        for (Dimension dimension : dimensions) {
            handleToken(dimension, TokenNameLBRACKET, spaceBeofreOpening, null);
            handleEmptyBrackets(dimension, spaceBetween);
        }
        return true;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        handleTokenBefore(node.getIndex(), TokenNameLBRACKET,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE);
        handleTokenAfter(node.getIndex(), TokenNameRBRACKET,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE, null);
        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        List<Expression> dimensions = node.dimensions();
        for (Expression dimension : dimensions) {
            handleTokenBefore(dimension, TokenNameLBRACKET, null,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION);
            handleTokenAfter(dimension, TokenNameRBRACKET,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, null);
        }
        return true;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        int openingBraceIndex = this.tm.firstIndexIn(node, TokenNameLBRACE);
        int closingBraceIndex = this.tm.lastIndexIn(node, TokenNameRBRACE);
        Token lastToken = this.tm.get(closingBraceIndex - 1);
        if (lastToken.tokenType == TokenNameLBRACE) {
            handleToken(this.tm.get(openingBraceIndex),
                    !(node.getParent() instanceof ArrayInitializer) && !(node.getParent() instanceof SingleMemberAnnotation)
                            ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER : null,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACES_IN_ARRAY_INITIALIZER);
        } else {
            boolean endsWithComma = lastToken.tokenType == TokenNameCOMMA;
            handleToken(this.tm.get(openingBraceIndex),
                    !(node.getParent() instanceof ArrayInitializer) && !(node.getParent() instanceof SingleMemberAnnotation) ?
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER : null,
                    !(endsWithComma && node.expressions().isEmpty()) ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER : null);
            handleCommas(node.expressions(), DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER);
            if (endsWithComma) {
                handleToken(lastToken, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER,
                        null); //DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER);
            }
            handleToken(this.tm.get(closingBraceIndex),
                    !(endsWithComma && node.expressions().isEmpty()) ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER : null, null);
        }
        return true;
    }

    @Override
    public boolean visit(ParameterizedType node) {
        List<Type> typeArguments = node.typeArguments();
        boolean hasArguments = !typeArguments.isEmpty();
        handleTokenAfter(node.getType(), TokenNameLESS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE,
                hasArguments ? DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE : null);
        if (hasArguments) {
            handleTokenAfter(typeArguments.get(typeArguments.size() - 1), TokenNameGREATER,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, null);
            handleCommas(node.typeArguments(),
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE);
        }
        return true;
    }

    @Override
    public boolean visit(TypeParameter node) {
        List<Type> typeBounds = node.typeBounds();
        for (int i = 1; i < typeBounds.size(); i++) {
            handleTokenBefore(typeBounds.get(i), TokenNameAND,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_AND_IN_TYPE_PARAMETER,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AND_IN_TYPE_PARAMETER);
        }
        return true;
    }

    @Override
    public boolean visit(WildcardType node) {
        handleToken(node, TokenNameQUESTION, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_QUESTION_IN_WILDCARD,
                node.getBound() != null ? null : DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_QUESTION_IN_WILDCARD);
        return true;
    }

    @Override
    public boolean visit(UnionType node) {
        List<Type> types = node.types();
        for (int i = 1; i < types.size(); i++)
            handleTokenBefore(types.get(i), TokenNameOR, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR,
                    DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR);
        return true;
    }

    @Override
    public boolean visit(Dimension node) {
        List<Annotation> annotations = node.annotations();
        if (!annotations.isEmpty())
            handleToken(annotations.get(0), TokenNameAT, null, null);
        return true;
    }

    @Override
    public boolean visit(TypeMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(ExpressionMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(SuperMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(CreationReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    private void handleTypeArguments(List<Type> typeArguments) {
        if (typeArguments.isEmpty())
            return;
        handleTokenBefore(typeArguments.get(0), TokenNameLESS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS);
        handleTokenAfter(typeArguments.get(typeArguments.size() - 1), TokenNameGREATER,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS);
        handleCommas(typeArguments, DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS);
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        handleTokenAfter(node.getLeftOperand(), TokenNameinstanceof, null, null);
        return true;
    }

    private void handleCommas(List<? extends ASTNode> nodes, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            for (int i = 1; i < nodes.size(); i++) {
                handleTokenBefore(nodes.get(i), TokenNameCOMMA, spaceBefore, spaceAfter);
            }
        }
    }

    private void handleToken(ASTNode node, int tokenType, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            Token token = this.tm.get(this.tm.findIndex(node.getStartPosition(), tokenType, true));
            // ^not the same as "firstTokenIn(node, tokenType)" - do not assert the token is inside the node
            handleToken(token, spaceBefore, spaceAfter);
        }
    }

    private void handleTokenBefore(ASTNode node, int tokenType, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            Token token = this.tm.firstTokenBefore(node, tokenType);
            handleToken(token, spaceBefore, spaceAfter);
        }
    }

    private void handleTokenAfter(ASTNode node, int tokenType, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            if (tokenType == TokenNameGREATER) {
                // there could be ">>" or ">>>" instead, get rid of them
                int index = this.tm.lastIndexIn(node, -1);
                for (int i = index; i < index + 2; i++) {
                    Token token = this.tm.get(i);
                    if (token.tokenType == TokenNameRIGHT_SHIFT || token.tokenType == TokenNameUNSIGNED_RIGHT_SHIFT) {
                        this.tm.remove(i);
                        for (int j = 0; j < (token.tokenType == TokenNameRIGHT_SHIFT ? 2 : 3); j++) {
                            this.tm.insert(i + j, new Token(token.originalStart + j, token.originalStart + j,
                                    TokenNameGREATER));
                        }
                    }
                }
            }
            Token token = this.tm.firstTokenAfter(node, tokenType);
            handleToken(token, spaceBefore, spaceAfter);
        }
    }

    private void handleToken(@NotNull Token token, @Nullable String spaceBefore, @Nullable String spaceAfter) {
        if (spaceBefore != null) {
            addFeature(spaceBefore, Direction.BEFORE, token);
        }
        if (spaceAfter != null) {
            addFeature(spaceAfter, Direction.AFTER, token);
        }
    }

    private boolean handleEmptyParens(ASTNode nodeBeforeParens, String insertSpace) {
        int openingIndex = this.tm.findIndex(nodeBeforeParens.getStartPosition(), TokenNameLPAREN, true);
        if (this.tm.get(openingIndex + 1).tokenType == TokenNameRPAREN) {
            addFeature(insertSpace, Direction.AFTER, this.tm.get(openingIndex));
            return true;
        }
        return false;
    }

    private boolean handleEmptyBrackets(ASTNode nodeContainingBrackets, String insertSpace) {
        int openingIndex = this.tm.firstIndexIn(nodeContainingBrackets, TokenNameLBRACKET);
        if (this.tm.get(openingIndex + 1).tokenType == TokenNameRBRACKET) {
            addFeature(insertSpace, Direction.AFTER, this.tm.get(openingIndex));
            return true;
        }
        return false;
    }

    private void handleSemicolon(ASTNode node) {
        Token lastToken = this.tm.lastTokenIn(node, -1);
        if (lastToken.tokenType == TokenNameSEMICOLON)
            addFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, Direction.BEFORE, lastToken);
    }

    private void handleSemicolon(List<ASTNode> nodes) {
        for (ASTNode node : nodes)
            handleSemicolon(node);
    }
}