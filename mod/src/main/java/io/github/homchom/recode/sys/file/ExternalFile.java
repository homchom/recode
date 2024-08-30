package io.github.homchom.recode.sys.file;

import java.nio.file.Path;

public enum ExternalFile {
    NBS_FILES(builder()
            .isDirectory(true)
            .setName("NBS Files")
            .build()),
    IMAGE_FILES(builder()
            .isDirectory(true)
            .setName("Images")
            .build()),
    TEMPLATE_DB(ExternalFileBuilder.nbt("Templates.nbt"));

    private final Path path;

    ExternalFile(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public static ExternalFileBuilder builder() {
        return new ExternalFileBuilder();
    }
}
