package com.wyrdtech.d.intellid;

import com.intellij.openapi.fileTypes.ExtensionFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * FileTypeFactory for D source file type.
 * Consumes ".d" files and sends them to DFileType
 */
public class DFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull final FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(DFileType.INSTANCE,
                new ExtensionFileNameMatcher(DFileType.SOURCE_EXTENSION),
                new ExtensionFileNameMatcher(DFileType.HEADER_EXTENSION));
    }
}
