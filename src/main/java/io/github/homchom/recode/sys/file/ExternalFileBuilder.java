package io.github.homchom.recode.sys.file;

import io.github.homchom.recode.Recode;
import net.minecraft.nbt.*;

import javax.annotation.Nullable;
import java.io.*;
import java.util.function.Consumer;

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

    public File buildFile(@Nullable Consumer<File> init) {
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
                if (init != null) init.accept(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File buildFile() {
        return buildFile(null);
    }

    static File nbt(String name) {
        return new ExternalFileBuilder()
                .isDirectory(false)
                .setName(name)
                .setFileType("nbt")
                .buildFile(file -> {
                    try {
                        NbtIo.write(new CompoundTag(), file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
