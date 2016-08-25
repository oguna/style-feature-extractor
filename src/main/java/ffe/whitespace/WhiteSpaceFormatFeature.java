package ffe.whitespace;

import ffe.Token;

public class WhiteSpaceFormatFeature {
    public final String format;
    public final WhiteSpaceOption value;
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
        return token.position + " : " +format + " = "+value;
    }
}
