package ffe.whitespace;

import ffe.token.Token;

public class WhiteSpaceFormatFeature {
    public final String format;
    public WhiteSpaceOption value;
    public final Token token;
    public final Direction direction;

    public WhiteSpaceFormatFeature(String format, WhiteSpaceOption value, Token token, Direction direction) {
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
