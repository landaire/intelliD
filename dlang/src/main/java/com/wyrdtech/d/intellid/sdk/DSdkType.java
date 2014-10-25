package com.wyrdtech.d.intellid.sdk;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.wyrdtech.d.intellid.DFileType;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Lander Brandt on 10/24/14.
 */
public class DSdkType extends SdkType {

    public static DSdkType getInstance() {
        return SdkType.findInstance(DSdkType.class);
    }

    public DSdkType() {
        super("D SDK");
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return DFileType.D_ICON;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        if (SystemInfo.isWindows) {
            // TODO: Windows SDK support
            return null;
        }
        else if (SystemInfo.isMac) {
            File homebrewRoot = new File("/usr/local/opt/dmd");
            if (homebrewRoot.exists()) {
                return homebrewRoot.getPath();
            }
            return null;
        }
        else if (SystemInfo.isLinux) {
            // TODO: Linux SDK support
            return null;
        }
        return null;
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        File dmd = getCompilerExecutable(path);
        return dmd.canExecute();
    }

    @NotNull
    public static File getCompilerExecutable(@NotNull String sdkHome) {
        return new File(FileUtil.join(new File(sdkHome, "bin").getAbsolutePath(), "dmd"));
    }

    @NotNull
    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        String version = getVersionString(sdkHome);
        if (version == null) return "Unknown D SDK version at " + sdkHome;
        return "D SDK " + version;
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        return DSdkUtil.getSdkVersion(sdkHome);
    }

    @Nullable
    @Override
    public String getDefaultDocumentationUrl(@NotNull Sdk sdk) {
        return null;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
    }

    @NonNls
    @Override
    public String getPresentableName() {
        return "D SDK";
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {

    }

    @VisibleForTesting
    @NotNull
    public static Sdk createMockSdk(@NotNull String sdkHome) {
        String release = DSdkUtil.getSdkVersion(sdkHome);
        Sdk sdk = new ProjectJdkImpl(release, getInstance());
        SdkModificator sdkModificator = sdk.getSdkModificator();
        sdkModificator.setHomePath(sdkHome);
        sdkModificator.setVersionString(release); // must be set after home path, otherwise setting home path clears the version string
        sdkModificator.commitChanges();
        return sdk;
    }

    private static void setupLocalSdkPaths(@NotNull SdkModificator sdkModificator) {
        String sdkHome = sdkModificator.getHomePath();

        File stdLibDir = new File(new File(sdkHome), "include");
        tryToProcessAsStandardLibraryDir(sdkModificator, stdLibDir);
    }

    private static boolean tryToProcessAsStandardLibraryDir(@NotNull SdkModificator sdkModificator, @NotNull File stdLibDir) {
        if (!isStandardLibraryDir(stdLibDir)) return false;
        VirtualFile dir = LocalFileSystem.getInstance().findFileByIoFile(stdLibDir);
        if (dir != null) {
            sdkModificator.addRoot(dir, OrderRootType.SOURCES);
            sdkModificator.addRoot(dir, OrderRootType.CLASSES);
        }
        return true;
    }

    private static boolean isStandardLibraryDir(@NotNull File dir) {
        return dir.isDirectory();
    }
}
