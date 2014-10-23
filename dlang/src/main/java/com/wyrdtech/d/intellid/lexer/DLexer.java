package com.wyrdtech.d.intellid.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import com.wyrdtech.parsed.lexer.token.BaseTokenFactory;
import com.wyrdtech.parsed.lexer.token.TokenFactory;
import com.wyrdtech.parsed.lexer.token.TokenType;
import dtool.parser.DeeLexer;
import dtool.parser.DeeTokenHelper;
import dtool.parser.DeeTokens;
import dtool.parser.common.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * IntelliJ Lexer for D, wrapper for the ParseD lexer.
 */
public class DLexer extends Lexer {
    private CharSequence buffer;

    private int start_offset;
    private int end_offset;
    private int cur_offset;

    private dtool.parser.DeeLexer lexer; // actual lexer
    private Token token; // current token

    private int state; // current token type, as ordinal

    @Override
    public void start(@NotNull final CharSequence buffer, final int startOffset, final int endOffset, final int initialState) {
        this.buffer = buffer;
        this.start_offset = startOffset;
        this.end_offset = endOffset;
        this.cur_offset = start_offset;

        token = null;

        this.lexer = new DeeLexer(buffer.subSequence(startOffset, endOffset).toString());

        token = lexer.next();
    }

    @Override
    public int getState() {
        return state;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        if (token == null) {
            return null;
        }

        return DTokenType.valueOf(token.getType());
    }

    @Override
    public int getTokenStart() {
        if (token == null) {
            return start_offset;
        }
        return start_offset + token.getStartPos();
    }

    @Override
    public int getTokenEnd() {
        if (token == null) {
            return start_offset;
        }
        return start_offset + token.getEndPos();
    }

    @Override
    public void advance() {
        token = lexer.next();
        cur_offset = start_offset + lexer.getLexingPosition();

        if (token != null) {
            state = token.getType().ordinal();
        } else {
            state = -1;
        }
    }

    @NotNull
    @Override
    public LexerPosition getCurrentPosition() {
        return new Position(cur_offset, getState());
    }

    @Override
    public void restore(@NotNull final LexerPosition position) {
        this.lexer = new DeeLexer(buffer.subSequence(position.getOffset(), end_offset).toString());
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return this.buffer;
    }

    @Override
    public int getBufferEnd() {
        return this.end_offset;
    }


    // Immutable version, in case it matters
    private static class Position implements LexerPosition {
        private final int offset;
        private final int state;

        public Position(int offset, int state) {
            this.offset = offset;
            this.state = state;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getState() {
            return state;
        }
    }
}
