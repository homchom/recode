package io.github.homchom.recode.mod.commands.arguments.types;

import java.io.File;
import java.util.*;

public class FileArgumentType {

    public static StringFuncArgumentType folder(File folder, boolean greedy) {
        return new StringFuncArgumentType(v -> {
            List<String> files = new ArrayList<>();

            for (File f : folder.listFiles()) {
                files.add(f.getName());
            }

            return files;
        }, true);
    }


}
