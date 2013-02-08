package com.wyrdtech.dlang.lexer;

/**
 *
 */
public class Token {

    public final TokenType type;

    public final int col;
    public final int line;

    public final int end_col;
    public final int end_line;

    public final Object literalValue;

    //TODO: final
    public Token next;

    public Token(TokenType type, int col, int line) {
        this.type = type;
        this.col = col;
        this.line = line;

        this.end_col = col+1;
        this.end_line = line;
        this.literalValue = null;
    }


    public Token(TokenType type, int col, int line, int length)
    {
        this.type = type;
        this.col = col;
        this.line = line;
        this.end_col = col + length;

        this.end_line = line;
        this.literalValue = null;
    }

    public Token(TokenType type, int col, int line, int length, Object literalValue)
    {
        this.type = type;
        this.col = col;
        this.line = line;
        this.end_col = col + length;
        this.literalValue = literalValue;

        this.end_line = line;
    }

    public Token(TokenType type, int col, int line, int end_col, int end_line, Object literalValue)
    {
        this.type = type;
        this.col = col;
        this.line = line;
        this.end_col = end_col;
        this.end_line = end_line;
        this.literalValue = literalValue;
    }

/*
    public Token getNext() {
        return next;
    }
    public void setNext(final Token next) {
        this.next = next;
    }
*/
}