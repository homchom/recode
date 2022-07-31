package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

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
        Minecraft mc = LegacyRecode.MC;
        BlockPos pos = new BlockPos(mc.hitResult.getLocation());
        if (!isPiston(pos)) {
            if (isPiston(pos.north())) {
                pos = pos.north();
            } else if (isPiston(pos.south())) {
                pos = pos.south();
            } else if (isPiston(pos.east())) {
                pos = pos.east();
            } else if (isPiston(pos.west())) {
                pos = pos.west();
            } else if (isPiston(pos.above())) {
                pos = pos.above();
            } else if (isPiston(pos.below())) {
                pos = pos.below();
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
        return LegacyRecode.MC.level.getBlockState(pos).getValue(BlockStateProperties.FACING) == Direction.NORTH;
    }

    private static boolean isPiston(BlockPos pos) {
        return LegacyRecode.MC.level.getBlockState(pos).is(Blocks.PISTON)
            || LegacyRecode.MC.level.getBlockState(pos).is(Blocks.STICKY_PISTON);
    }

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("partnerbracket")
            .executes(PartnerBracketCommand::exec)
        );


        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, outline) -> {
            if (active) {
                try {

                    PoseStack matrix = ctx.matrixStack();
                    matrix.pushPose();
                    matrix.scale(1.0005f, 1.0005f, 1.0005f);
                    Vec3 vec = ctx.camera().getPosition();

                    matrix.translate(-vec.x, -vec.y, -vec.z);

                    //TODO: someone should recode this cuz im bad at rendering stuff
                    matrix.pushPose();
                    matrix.translate(p1.getX(),p1.getY(),p1.getZ());

                    OutlineBufferSource outlineVertexConsumerProvider = mc.renderBuffers().outlineBufferSource();
                    outlineVertexConsumerProvider.setColor(255, 255, 255, 150);

                    mc.getBlockRenderer().renderSingleBlock(
                        Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
                        ctx.matrixStack(), outlineVertexConsumerProvider,
                        16777215,
                        655360
                    );
                    matrix.popPose();

                    matrix.pushPose();
                    matrix.translate(p2.getX(),p2.getY(),p2.getZ());
                    mc.getBlockRenderer().renderSingleBlock(
                            Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
                            ctx.matrixStack(), outlineVertexConsumerProvider,
                            16777215,
                            655360
                    );

                    matrix.popPose();

                    matrix.popPose();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            return true;
        });
    }

    @Override
    public String getDescription() {
        return "[blue]/partnerbracket[reset]\n"
                + "\n"
                + "Highlights where the end/start of the piston bracket you are looking is.";
    }

    @Override
    public String getName() {
        return "/partnerbracket";
    }
}
