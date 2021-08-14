package io.github.codeutilities.mod.commands.arguments.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileArgumentType {

    public static StringFuncArgumentType folder(File folder) {
        return new StringFuncArgumentType(v -> {
            List<String> files = new ArrayList<>();

            for (File f : folder.listFiles()) {
                files.add(f.getName());
            }

            return files;
        });
    }


}
