package io.github.codeutilities.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class PartnerBracketCommand extends Command {

    private static boolean active = false;
    private static BlockPos p1;
    private static BlockPos p2;

    private static int exec(CommandContext<FabricClientCommandSource> ctx) {
        exec();
        return 1;
    }

    public static void exec() {
        if (active) {
            active = false;
            ChatUtil.sendMessage("Disabled Bracket view.", ChatType.INFO_BLUE);
            return;
        }
        MinecraftClient mc = CodeUtilities.MC;
        BlockPos pos = new BlockPos(mc.crosshairTarget.getPos());
        if (!isPiston(pos)) {
            if (isPiston(pos.north())) {
                pos = pos.north();
            } else if (isPiston(pos.south())) {
                pos = pos.south();
            } else if (isPiston(pos.east())) {
                pos = pos.east();
            } else if (isPiston(pos.west())) {
                pos = pos.west();
            } else if (isPiston(pos.up())) {
                pos = pos.up();
            } else if (isPiston(pos.down())) {
                pos = pos.down();
            }
        }
        if (isPiston(pos)) {
            boolean north = getDir(pos);

            p1 = pos;
            p2 = pos;

            int depth = 1;

            for (int i = 0; i < 300; i++) {
                if (north) {
                    p2 = p2.north();
                } else {
                    p2 = p2.south();
                }

                if (isPiston(p2)) {
                    if (getDir(p2) == north) {
                        depth++;
                    } else {
                        depth--;
                        if (depth == 0) {
                            active = true;
                            ChatUtil.sendMessage("Partner Found!", ChatType.SUCCESS);
                            return;
                        }
                    }
                }
            }

            ChatUtil.sendMessage("No Partner bracket found", ChatType.FAIL);
        } else {
            ChatUtil.sendMessage("You need to look at a piston!", ChatType.FAIL);
        }
    }

    private static boolean getDir(BlockPos pos) {
        return CodeUtilities.MC.world.getBlockState(pos).get(Properties.FACING) == Direction.NORTH;
    }

    private static boolean isPiston(BlockPos pos) {
        return CodeUtilities.MC.world.getBlockState(pos).isOf(Blocks.PISTON)
            || CodeUtilities.MC.world.getBlockState(pos).isOf(Blocks.STICKY_PISTON);
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("partnerbracket")
            .executes(PartnerBracketCommand::exec)
        );


        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, outline) -> {
            if (active) {
                try {

                    MatrixStack matrix = ctx.matrixStack();
                    matrix.push();
                    matrix.scale(1.0005f, 1.0005f, 1.0005f);
                    Vec3d vec = ctx.camera().getPos();

                    matrix.translate(-vec.x, -vec.y, -vec.z);

                    //TODO: someone should recode this cuz im bad at rendering stuff
                    matrix.push();
                    matrix.translate(p1.getX(),p1.getY(),p1.getZ());

                    OutlineVertexConsumerProvider outlineVertexConsumerProvider = mc.getBufferBuilders().getOutlineVertexConsumers();
                    outlineVertexConsumerProvider.setColor(255, 255, 255, 150);

                    mc.getBlockRenderManager().renderBlockAsEntity(
                        Blocks.WHITE_STAINED_GLASS.getDefaultState(),
                        ctx.matrixStack(), outlineVertexConsumerProvider,
                        16777215,
                        655360
                    );
                    matrix.pop();

                    matrix.push();
                    matrix.translate(p2.getX(),p2.getY(),p2.getZ());
                    mc.getBlockRenderManager().renderBlockAsEntity(
                            Blocks.WHITE_STAINED_GLASS.getDefaultState(),
                            ctx.matrixStack(), outlineVertexConsumerProvider,
                            16777215,
                            655360
                    );

                    matrix.pop();

                    matrix.pop();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            return true;
        });
    }

    @Override
    public String getDescription() {
        return "TODO: Add description";
    }

    @Override
    public String getName() {
        return "/partnerbracket";
    }
}
