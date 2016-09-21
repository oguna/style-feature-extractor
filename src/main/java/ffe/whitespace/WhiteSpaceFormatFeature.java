package ffe.whitespace;

import ffe.token.Token;

public class WhiteSpaceFormatFeature {
    public final String format;
    public final WhiteSpaceOption value;
    public final Token token;

    public WhiteSpaceFormatFeature(String format, WhiteSpaceOption value, Token token) {
        this.format = format;
        this.value = value;
        this.token = token;
    }

    @Override
    public String toString() {
        return token.originalStart + " : " + format + " = " + value;
    }
}
