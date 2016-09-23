package ffe.whitespace;

import ffe.token.Token;

public class WhiteSpaceFormatFeature {
    public final String format;
    public WhiteSpaceOption value;
    public final Token token;
    public final Direction direction;

    private final static String prefixToRemove = "org.eclipse.jdt.core.formatter.";

    public WhiteSpaceFormatFeature(String format, WhiteSpaceOption value, Token token, Direction direction) {
        if (format.startsWith(prefixToRemove)) {
            format = format.substring(prefixToRemove.length());
        }
        this.format = format;
        this.value = value;
        this.token = token;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return token.originalStart + " : " + format + " = " + value;
    }
}
