package ffe.gui;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class JavaSyntaxHighlighter {
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        try {
            Scanner scanner = new Scanner();
            scanner.setSource(text.toCharArray());
            scanner.sourceLevel = ClassFileConstants.JDK1_8;
            scanner.tokenizeComments = true;
            scanner.tokenizeWhiteSpace = true;
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
            int tokenType;
            while ((tokenType = scanner.getNextToken()) != TokenNameEOF) {
                String styleClass;
                switch (tokenType) {
                    case TokenNameabstract:
                    case TokenNameassert:
                    case TokenNameboolean:
                    case TokenNamebreak:
                    case TokenNamebyte:
                    case TokenNamecase:
                    case TokenNamecatch:
                    case TokenNamechar:
                    case TokenNameclass:
                    case TokenNamecontinue:
                    case TokenNameconst:
                    case TokenNamedefault:
                    case TokenNamedo:
                    case TokenNamedouble:
                    case TokenNameelse:
                    case TokenNameenum:
                    case TokenNameextends:
                    case TokenNamefalse:
                    case TokenNamefinal:
                    case TokenNamefinally:
                    case TokenNamefloat:
                    case TokenNamefor:
                    case TokenNamegoto:
                    case TokenNameif:
                    case TokenNameimplements:
                    case TokenNameimport:
                    case TokenNameinstanceof:
                    case TokenNameint:
                    case TokenNameinterface:
                    case TokenNamelong:
                    case TokenNamenative:
                    case TokenNamenew:
                    case TokenNamenull:
                    case TokenNamepackage:
                    case TokenNameprivate:
                    case TokenNameprotected:
                    case TokenNamepublic:
                    case TokenNamereturn:
                    case TokenNameshort:
                    case TokenNamestatic:
                    case TokenNamestrictfp:
                    case TokenNamesuper:
                    case TokenNameswitch:
                    case TokenNamesynchronized:
                    case TokenNamethis:
                    case TokenNamethrow:
                    case TokenNamethrows:
                    case TokenNametransient:
                    case TokenNametrue:
                    case TokenNametry:
                    case TokenNamevoid:
                    case TokenNamevolatile:
                    case TokenNamewhile:
                        styleClass = "keyword";
                        break;
                    case TokenNameSEMICOLON:
                        styleClass = "semicolon";
                        break;
                    case TokenNameStringLiteral:
                        styleClass = "string";
                        break;
                    case TokenNameRBRACKET:
                    case TokenNameLBRACKET:
                        styleClass = "bracket";
                        break;
                    case TokenNameRBRACE:
                    case TokenNameLBRACE:
                        styleClass = "brace";
                        break;
                    case TokenNameRPAREN:
                    case TokenNameLPAREN:
                        styleClass = "paren";
                        break;
                    case TokenNameCOMMENT_BLOCK:
                    case TokenNameCOMMENT_JAVADOC:
                    case TokenNameCOMMENT_LINE:
                        styleClass = "comment";
                        break;
                    case TokenNameWHITESPACE:
                        styleClass = null;
                        break;
                    default:
                        styleClass = null;
                }
                int length = scanner.getCurrentTokenEndPosition() - scanner.getCurrentTokenStartPosition() + 1;
                if (styleClass == null) {
                    spansBuilder.add(Collections.emptyList(), length);
                } else {
                    spansBuilder.add(Collections.singleton(styleClass), length);
                }
            }
            return spansBuilder.create();
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }
}
