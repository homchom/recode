package io.github.homchom.recode.sys.file;

import java.io.File;

public enum ExternalFile {
    NBS_FILES(new ExternalFileBuilder()
            .isDirectory(true)
            .setName("NBS Files")
            .buildFile()),
    IMAGE_FILES(new ExternalFileBuilder()
            .isDirectory(true)
            .setName("Images")
            .buildFile()),
    TEMPLATE_DB(ExternalFileBuilder.nbt("Templates"));

    private final File file;

    ExternalFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
