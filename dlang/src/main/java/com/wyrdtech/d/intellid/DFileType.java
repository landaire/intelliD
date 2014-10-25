package com.wyrdtech.d.intellid;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import dtool.engine.modules.ModuleNamingRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 *
 */
public class DFileType extends LanguageFileType {

    public static final LanguageFileType INSTANCE = new DFileType();

    public static final String SOURCE_EXTENSION = "d";
    public static final String HEADER_EXTENSION = "di";

    public static final Icon D_ICON = IconLoader.getIcon("/icons/d.png");

    private DFileType() {
        super(DLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "D";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "D files";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return SOURCE_EXTENSION;
    }

    public String getHeaderExtension() { return HEADER_EXTENSION;}

    @Nullable
    @Override
    public Icon getIcon() {
        return D_ICON;
    }
}
