package com.wyrdtech.d.intellid.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.wyrdtech.d.intellid.lexer.DTokenType;
import org.jetbrains.annotations.NotNull;

public class DBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = {
            bracePairFromTokenSet(DTokenType.PARENS, true),
            bracePairFromTokenSet(DTokenType.BRACKET, false),
            bracePairFromTokenSet(DTokenType.BRACKET, true),
    };

    private static BracePair bracePairFromTokenSet(TokenSet set, boolean isStructural) {
        IElementType[] types = set.getTypes();

        return new BracePair(types[0], types[1], isStructural);
    }

    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, IElementType iElementType1) {
        return false;
    }

    @Override
    public int getCodeConstructStart(PsiFile psiFile, int i) {
        return i;
    }
}
