package com.wyrdtech.d.intellid.sdk;

import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ApplicationLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Lander Brandt on 10/25/14.
 */
public class DSdk {
    static final String D_SDK_GLOBAL_LIB_NAME = "D SDK";
    private static final String UNKNOWN_VERSION = "unknown";

    private final @NotNull
    String myHomePath;
    private final @NotNull String myVersion;
    private final @NotNull String myGlobalLibName;

    private DSdk(@NotNull final String homePath, @NotNull final String version, final @NotNull String globalLibName) {
        myHomePath = homePath;
        myVersion = version;
        myGlobalLibName = globalLibName;
    }

    @NotNull
    public String getHomePath() {
        return myHomePath;
    }

    @NotNull
    public String getVersion() {
        return myVersion;
    }

    @NotNull
    public String getGlobalLibName() {
        return myGlobalLibName;
    }

    @Nullable
    public static DSdk getGlobalDartSdk() {
        return findDartSdkAmongGlobalLibs(ApplicationLibraryTable.getApplicationTable().getLibraries());
    }

    public static DSdk findDartSdkAmongGlobalLibs(final Library[] globalLibraries) {
        for (final Library library : globalLibraries) {
            final String libraryName = library.getName();
            if (libraryName != null && libraryName.startsWith(D_SDK_GLOBAL_LIB_NAME)) {
                for (final VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
                    if (DSdkLibraryPresentationProvider.isDSdkLibRoot(root)) {
                        final String homePath = root.getParent().getPath();
                        final String version = StringUtil.notNullize(DSdkUtil.getSdkVersion(homePath), UNKNOWN_VERSION);
                        return new DSdk(homePath, version, libraryName);
                    }
                }
            }
        }

        return null;
    }
}
