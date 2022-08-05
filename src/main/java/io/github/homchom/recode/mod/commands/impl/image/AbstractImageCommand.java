package io.github.homchom.recode.mod.commands.impl.image;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.PathArgumentType;
import io.github.homchom.recode.sys.file.ExternalFile;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import java.nio.file.Path;
import java.util.function.Function;

public abstract class AbstractImageCommand extends Command {
    protected ArgumentCommandNode<FabricClientCommandSource, Path> fileArgument(Function<Path, Integer> executor) {
        return ArgBuilder.argument("file", PathArgumentType.folder(ExternalFile.IMAGE_FILES.getPath(), true))
                .executes(ctx -> {
                    Path path = PathArgumentType.getPath(ctx, "file");

                    return executor.apply(path);
                })
                .build();
    }
}
