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
    TEMPLATE_DB(new ExternalFileBuilder()
            .isDirectory(false)
            .setName("Templates")
            .setFileType("nbt")
            .buildFile()),
    PLOTS_DB(new ExternalFileBuilder()
            .isDirectory(false)
            .setName("Plots")
            .setFileType("nbt")
            .buildFile());

    private final File file;

    ExternalFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }


}
