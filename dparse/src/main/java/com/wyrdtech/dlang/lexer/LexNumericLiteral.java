package com.wyrdtech.dlang.lexer;

import java.io.IOException;

/**
 * Encapsulates logic for lexing out a numerical literal from an input stream.
 *
 * TODO: lexer exception?
 */
public class LexNumericLiteral {

    // Purely static members for now
    private LexNumericLiteral() {}

    public static Token read(LexerStream in_stream) throws IOException, LexerException {
        int start_line = in_stream.getLine();
        int start_col = in_stream.getCol();
        int length = 0;

/*
        int c = in_stream.read();
        if (c == -1) {
            // end of stream!
            throw new LexerException(in_stream.getLine(), in_stream.getCol(), "Unexpected end of input stream when parsing numeric literal");
        }
*/

        // Parsing decisions are made on the not-yet-consumed next value, to
        // avoid consuming character after end of literal.
        int n = in_stream.peek();
        if (n == -1) {
            // end of stream!
            throw new LexerException(in_stream.getLine(), in_stream.getCol(), "Unexpected end of input stream when parsing numeric literal");
        }

        char next = (char)n;

//        char cur = (char)c;
//        if (!Character.isDigit(cur) && cur != '.')
        if (!Character.isDigit(next) && next != '.')
        {
            throw new LexerException(in_stream.getLine(), in_stream.getCol(), "Numeric literals can only start with a digit (0-9) or a dot ('.')");
        }


        // Build up value string
        StringBuilder sb = new StringBuilder();


        int base = 10;
        int exponent = 0;

        boolean is_float = false;
        boolean is_long = false;
        boolean is_unsigned = false;
        boolean is_imaginary = false;

        boolean allow_suffix = true;

        String prefix = "";
        String suffix = "";
        String exp_suffix = "";


/*
        LiteralSubformat subFmt = 0;
*/

        // Check for hex/binary
        if (next == '0')
        {
            sb.append((char)in_stream.read());
            length++;

            next = (char)in_stream.peek();

            if (next == 'x' || next == 'X') // Hex values
            {
                prefix = "0x";
                base = 16;

                sb.setLength(0); // Remove '0' from the string value

                in_stream.read(); // skip 'x'
                length++;

                next = (char)in_stream.peek();
            }
            else if (next == 'b' || next == 'B') // Bin values
            {
                prefix = "0b";
                base = 2;

                sb.setLength(0); // Remove '0' from the string value

                in_stream.read(); // skip 'b'
                length++;

                next = (char)in_stream.peek();
            }
            // else base 10
        }

        // Read literal into string builder
        while (is_legal_digit(next, base)) {
            char cur = (char)in_stream.read(); // consume, same char as 'next'
            length++;

            if (cur != '_') {
                sb.append(cur);
            }
            next = (char)in_stream.peek();
        }

        // Check for '.' and read rest of num if present, or tokenize
        // as itself or '..' as appropriate
        Token next_token = null;
        if (next == '.') {
            char cur = (char)in_stream.read(); // '.'
            length++;

            next = (char)in_stream.peek();

            if (is_legal_digit(next, base)) {
                is_float = true;

                sb.append(cur);
                do {
                    cur = (char)in_stream.read(); // consume, same char as 'next'
                    length++;

                    if (cur != '_') {
                        sb.append(cur);
                    }
                    next = (char)in_stream.peek();
                } while (is_legal_digit(next, base));
            }
            else {
                allow_suffix = false;
                length--; // part of next token

                // '.' was after literal, but we already consumed it so tokenize
                if (next == '.') {
                    next_token = new Token(TokenType.DoubleDot,  in_stream.getCol(), in_stream.getLine(), 2);
                    in_stream.read();  // eat up the second dot
                }
                else if (is_identifier_part(next)) {
                    next_token = new Token(TokenType.Dot, in_stream.getCol(), in_stream.getLine(), 1);
                }

            }
        }

        // Possible exponent part
        if (is_exp_marker(next, base))
        {
            // exponent means must be a float
            is_float = true;

            StringBuilder exp = new StringBuilder();
            exp.append(next);

            in_stream.read();
            length++;

            next = (char)in_stream.peek();

            if (next == '-' || next == '+') {
                exp.append(next);
                in_stream.read();
                length++;
            }

            next = (char)in_stream.peek();
            while (is_legal_digit(next, 10)) { // always in decimal notation
                char cur = (char)in_stream.read();
                length++;
                if (cur != '_') {
                    exp.append(cur);
                }
                next = (char)in_stream.peek();
            }

            exp_suffix = exp.toString();

            // knock of 'e' or 'p' and parse to int
            exponent = Integer.parseInt(exp.substring(1), 10);

            next = (char)in_stream.peek();
        }

        // Suffixes
        if (allow_suffix) {

            // 'L' is valid for both float and integer
            if (next == 'L') {
                is_long = true;
//                subFmt |= LiteralSubformat.Long;

                suffix += next;

                in_stream.read();
                length++;
                next = (char)in_stream.peek();
            }

            // Check for float tag, mutually exclusive with 'L'
            if ((next == 'f' || next == 'F') && !is_long) {
                is_float = true;

                suffix += next;

                in_stream.read();
                length++;
                next = (char)in_stream.peek();
            }

            // Check for unsigned, only valid of not a float
            if ((next == 'u' || next == 'U') && !is_float) {
                is_unsigned = true;
//                subFmt |= LiteralSubformat.Unsigned;

                suffix += next;

                in_stream.read();
                length++;
                next = (char)in_stream.peek();
            }

            // Check for trailing 'L'
            if (next == 'L'  && !is_long && !is_float) {
                is_long = true;
//                subFmt |= LiteralSubformat.Long;

                suffix += next;

                in_stream.read();
                length++;
                next = (char)in_stream.peek();
            }

            // imaginary suffix, must be after all others
            if (next == 'i' && !is_unsigned) {
                is_float = true;
                is_imaginary = true;
//                subFmt |= LiteralSubformat.Imaginary;

                suffix += next;

                in_stream.read();
                length++;
                next = (char)in_stream.peek();
            }

        }

        // Determine actual value
        Object value;

        // explicitly specified D types, we ignore this for now
        if (is_float && is_long) { /* real */ }
        else if (is_float) { /* float */ }
        else if (is_unsigned && is_long) { /* ulong */ }
        else if (is_unsigned) { /* uint */ }
        else if (is_long) { /* long */ }

        if (is_imaginary) { /* imaginary */ }


        //TODO: register out-of-bounds errors
        if (is_float) {
            try {
                if (base == 16) {
                    value = Double.valueOf(sb.toString()) * Math.pow(2, (double)exponent);
                } else {
                    value = Double.valueOf(sb.toString() + exp_suffix);
                }
            } catch (NumberFormatException e) {
                value = Double.MAX_VALUE;
            }
        }
        else {
            try {
                //noinspection UnnecessaryBoxing
                value = Long.valueOf(Long.parseLong(sb.toString(), base));
            } catch (NumberFormatException e) {
                value = Long.MAX_VALUE;
            }
        }


        // Create token
        Token token = new Token(TokenType.Literal,
                                start_col,
                                start_line,
                                length,
                                value);
        token.next = next_token;

        return token;

    }


    private static boolean is_exp_marker(final char c, final int base) {
        if (base == 16) {
            return (c == 'p' || c == 'P');
        }
        return (c == 'e' || c == 'E');
    }

    /**
     * Tests if the given character is a valid representation in the given
     * numerical base.
     * An underscore character ('_') will always return true.
     * @param d character to check
     * @param base base to consider
     * @return true if digit is valid for given base, false otherwise
     */
    private static boolean is_legal_digit(final char d, final int base)
    {
        if (d == '_') {
            return true;
        }
        if (base == 10) {
            return Character.isDigit(d);
        }
        if (base == 2) {
            return is_bin(d);
        }
/* Octal no longer supported except in string literals
        if (base == 8) {
            return is_oct(d);
        }
*/
        if (base == 16) {
            return is_hex(d);
        }

        return false;
    }

/*
    private static boolean is_oct(char digit)
    {
        return Character.isDigit(digit) && digit != '9' && digit != '8';
    }
*/

    private static boolean is_hex(char digit)
    {
        return (digit >= '0' && digit <= '9') || ('A' <= digit && digit <= 'F') || ('a' <= digit && digit <= 'f');
    }

    private static boolean is_bin(char digit)
    {
        return digit == '0' || digit == '1';
    }


    // exponent markers count too
    private static boolean is_identifier_part(int ch)
    {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        if (ch == '_') {
            return true;
        }

/*
        switch(ch)
        {
            case ' ':
            case '@':
            case '/':
            case '(':
            case ')':
            case '[':
            case ']':
            case '{':
            case '}':
            case '=':
            case '\"':
            case '\'':
            case -1:
                return false;
            default:
                return char.IsLetterOrDigit((char)ch); // accept unicode letters
        }
*/
        return false;
    }

}
