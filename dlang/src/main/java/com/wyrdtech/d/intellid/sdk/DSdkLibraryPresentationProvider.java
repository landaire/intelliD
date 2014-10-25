package com.wyrdtech.d.intellid.sdk;

import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.wyrdtech.d.intellid.DFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Created by Lander Brandt on 10/25/14.
 */
public class DSdkLibraryPresentationProvider extends LibraryPresentationProvider<DummyLibraryProperties> {

    private static final LibraryKind KIND = LibraryKind.create("d");

    protected DSdkLibraryPresentationProvider() {
        super(KIND);
    }

    @Nullable
    public Icon getIcon() {
        return DFileType.D_ICON;
    }

    @Nullable
    public DummyLibraryProperties detect(@NotNull final List<VirtualFile> classesRoots) {
        for (VirtualFile root : classesRoots) {
            if (isDSdkLibRoot(root)) {
                return DummyLibraryProperties.INSTANCE;
            }
        }
        return null;
    }

    static boolean isDSdkLibRoot(final VirtualFile root) {
        return root.isInLocalFileSystem() &&
                root.isDirectory() &&
                root.getName().equals("lib");
    }
}
