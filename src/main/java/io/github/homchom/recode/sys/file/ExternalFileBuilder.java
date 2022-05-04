package io.github.homchom.recode.sys.file;

import io.github.homchom.recode.Recode;

import java.io.*;

public class ExternalFileBuilder {

    String fileName;
    String fileType = "unk";
    boolean directory = false;

    public ExternalFileBuilder setName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ExternalFileBuilder setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public ExternalFileBuilder isDirectory(boolean directory) {
        this.directory = directory;
        return this;
    }

    public File buildFile() {
        File mainFile = new File(Recode.MOD_NAME);
        if (!mainFile.exists()) {
            mainFile.mkdir();
        }

        File file = new File(mainFile, fileName + (directory ? "" : "." + fileType));

        try {
            if (!file.exists()) {
                if (directory) {
                    file.mkdir();
                } else {
                    file.createNewFile();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

    }
}
