package ffe;

public class Token {
    public final int tokenType;
    public final int position;
    public final int length;
    public final int line;

    public Token(int tokenType, int position, int length, int line) {
        this.tokenType = tokenType;
        this.position = position;
        this.length = length;
        this.line = line;
    }
}
