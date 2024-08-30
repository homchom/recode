package io.github.homchom.recode.sys.file;

import io.github.homchom.recode.ModConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ExternalFileBuilder {
    String fileName;
    boolean directory = false;

    public ExternalFileBuilder setName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ExternalFileBuilder isDirectory(boolean directory) {
        this.directory = directory;
        return this;
    }

    private Path getMainDir() throws IOException {
        Path path = FabricLoader.getInstance().getGameDir().resolve(ModConstants.MOD_NAME);
        if (!Files.isDirectory(path)) Files.createDirectory(path);
        return path;
    }

    public Path buildRaw(@Nullable Consumer<Path> init) throws IOException {
        Path path = getMainDir().resolve(fileName);

        // Yes, I know this is very verbose, but it's very extensive, and is the same logic
        // used by the JRE internally.
        if (directory) {
            try {
                Files.createDirectory(path);
            } catch (FileAlreadyExistsException x) {
                if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) throw x;
            }
        } else {
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException x) {
                if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) throw x;
            }
        }
        if (init != null) init.accept(path);

        return path;
    }

    public Path buildRaw() throws IOException {
        return buildRaw(null);
    }

    public Path build(@Nullable Consumer<Path> init) {
        try {
            return buildRaw(init);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path build() {
        return build(null);
    }

    static Path nbt(String name) {
        return new ExternalFileBuilder()
                .isDirectory(false)
                .setName(name)
                .build(path -> {
                    try {
                        NbtIo.write(new CompoundTag(), path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
