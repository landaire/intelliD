package com.wyrdtech.dlang.lexer;

import java.io.IOException;

/**
 *
 */
public class LexOperator {

    private LexOperator() {}

    public static Token read(LexerStream in_stream) throws IOException, LexerException {
        int start_line = in_stream.getLine();
        int start_col = in_stream.getCol();

        int x = start_col;
        int y = start_line;


        int next = in_stream.peek();
        if (next == -1) {
            // end of stream!
            throw new LexerException(in_stream.getLine(), in_stream.getCol(), "Unexpected end of input stream when parsing operator");
        }

        // If we have a '.' may be a literal, in which case don't want to consume here
        if (next == '.') {
            int tmp = in_stream.peek(2);
            if (tmp > 0 && Character.isDigit(tmp)) {
                return LexNumericLiteral.read(in_stream);
            }
        }

        // Everything else we consume the first char, check it and next
        char cur = (char)in_stream.read();
        next = in_stream.peek();

        switch (cur)
        {
            case '+':
                switch (next)
                {
                    case '+':
                        in_stream.read();
                        return new Token(TokenType.Increment, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.PlusAssign, x, y, 2);
                }
                return new Token(TokenType.Plus, x, y);
            case '-':
                switch (next)
                {
                    case '-':
                        in_stream.read();
                        return new Token(TokenType.Decrement, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.MinusAssign, x, y, 2);
/*
                    case '>':
                        in_stream.read();
                        return new Token(TokenType.TildeAssign, x, y, 2);
*/
                }
                return new Token(TokenType.Minus, x, y);
            case '*':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.TimesAssign, x, y, 2);
                    default:
                        break;
                }
                return new Token(TokenType.Times, x, y);
            case '/':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.DivAssign, x, y, 2);
                }
                return new Token(TokenType.Div, x, y);
            case '%':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.ModAssign, x, y, 2);
                }
                return new Token(TokenType.Mod, x, y);
            case '&':
                switch (next)
                {
                    case '&':
                        in_stream.read();
                        return new Token(TokenType.LogicalAnd, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.BitwiseAndAssign, x, y, 2);
                }
                return new Token(TokenType.BitwiseAnd, x, y);
            case '|':
                switch (next)
                {
                    case '|':
                        in_stream.read();
                        return new Token(TokenType.LogicalOr, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.BitwiseOrAssign, x, y, 2);
                }
                return new Token(TokenType.BitwiseOr, x, y);
            case '^':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.XorAssign, x, y, 2);
                    case '^':
                        in_stream.read();
                        if (in_stream.peek() == '=')
                        {
                            in_stream.read();
                            return new Token(TokenType.PowAssign, x, y, 3);
                        }
                        return new Token(TokenType.Pow, x, y, 2);
                }
                return new Token(TokenType.Xor, x, y);
            case '!':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.NotEqual, x, y, 2); // !=

                    case '<':
                        in_stream.read();
                        switch (in_stream.peek())
                        {
                            case '=':
                                in_stream.read();
                                return new Token(TokenType.UnorderedOrGreater, x, y, 3); // !<=
                            case '>':
                                in_stream.read();
                                switch (in_stream.peek())
                                {
                                    case '=':
                                        in_stream.read();
                                        return new Token(TokenType.Unordered, x, y, 4); // !<>=
                                }
                                return new Token(TokenType.UnorderedOrEqual, x, y, 3); // !<>
                        }
                        return new Token(TokenType.UnorderedGreaterOrEqual, x, y, 2); // !<

                    case '>':
                        in_stream.read();
                        switch (in_stream.peek())
                        {
                            case '=':
                                in_stream.read();
                                return new Token(TokenType.UnorderedOrLess, x, y, 3); // !>=
                            default:
                                break;
                        }
                        return new Token(TokenType.UnorderedLessOrEqual, x, y, 2); // !>

                }
                return new Token(TokenType.Not, x, y);
            case '~':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.TildeAssign, x, y, 2);
                }
                return new Token(TokenType.Tilde, x, y);
            case '=':
                switch (next)
                {
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.Equal, x, y, 2);
                    case '>':
                        in_stream.read();
                        return new Token(TokenType.GoesTo, x, y, 2);
                }
                return new Token(TokenType.Assign, x, y);
            case '<':
                switch (next)
                {
                    case '<':
                        in_stream.read();
                        switch (in_stream.peek())
                        {
                            case '=':
                                in_stream.read();
                                return new Token(TokenType.ShiftLeftAssign, x, y, 3);
                            default:
                                break;
                        }
                        return new Token(TokenType.ShiftLeft, x, y, 2);
                    case '>':
                        in_stream.read();
                        switch (in_stream.peek())
                        {
                            case '=':
                                in_stream.read();
                                return new Token(TokenType.LessEqualOrGreater, x, y, 3);
                            default:
                                break;
                        }
                        return new Token(TokenType.LessOrGreater, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.LessEqual, x, y, 2);
                }
                return new Token(TokenType.LessThan, x, y);
            case '>':
                switch (next)
                {
                    case '>':
                        in_stream.read();
                        int p = in_stream.peek();
                        if (p != -1)
                        {
                            switch ((char)p)
                            {
                                case '=':
                                    in_stream.read();
                                    return new Token(TokenType.ShiftRightAssign, x, y, 3);
                                case '>':
                                    in_stream.read();
                                    int q = in_stream.peek();
                                    if (q != -1 && q == '=')
                                    {
                                                in_stream.read();
                                                return new Token(TokenType.TripleRightShiftAssign, x, y, 4);
                                    }
                                    return new Token(TokenType.ShiftRightUnsigned, x, y, 3); // >>>
                            }
                        }
                        return new Token(TokenType.ShiftRight, x, y, 2);
                    case '=':
                        in_stream.read();
                        return new Token(TokenType.GreaterEqual, x, y, 2);
                }
                return new Token(TokenType.GreaterThan, x, y);
            case '?':
                return new Token(TokenType.Question, x, y);
            case '$':
                return new Token(TokenType.Dollar, x, y);
            case ';':
                return new Token(TokenType.Semicolon, x, y);
            case ':':
                return new Token(TokenType.Colon, x, y);
            case ',':
                return new Token(TokenType.Comma, x, y);
            case '.':
                if (next == '.')
                {
                    in_stream.read();
                    int p = in_stream.peek();
                    if (p != -1 && p == '.') {
                        in_stream.read();
                        return new Token(TokenType.TripleDot, x, y, 3);
                    }
                    return new Token(TokenType.DoubleDot, x, y, 2);
                }
                return new Token(TokenType.Dot, x, y);
            case ')':
                return new Token(TokenType.CloseParenthesis, x, y);
            case '(':
                return new Token(TokenType.OpenParenthesis, x, y);
            case ']':
                return new Token(TokenType.CloseSquareBracket, x, y);
            case '[':
                return new Token(TokenType.OpenSquareBracket, x, y);
            case '}':
                return new Token(TokenType.CloseCurlyBrace, x, y);
            case '{':
                return new Token(TokenType.OpenCurlyBrace, x, y);
            default:
                return null;
        }
    }

}