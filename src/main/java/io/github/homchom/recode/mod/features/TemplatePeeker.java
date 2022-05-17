/*
package io.github.homchom.recode.mod.features;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.hypercube.codeaction.*;
import io.github.homchom.recode.sys.hypercube.templates.*;
import io.github.homchom.recode.sys.renderer.block.BlockRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;

public class TemplatePeeker implements ILoader {

    public static boolean templatePreview = false;
    public static boolean templateFits = true;
    public static HashMap<String, Block> blockTypes = new HashMap<>();
    public static HashSet<String> noStones = new HashSet<>();
    public static HashSet<String> noChests = new HashSet<>();
    private static BlockRenderer blockRenderer = new BlockRenderer();

    @Override
    public void load() {

        blockTypes.put("func", Blocks.LAPIS_BLOCK);
        blockTypes.put("set_var", Blocks.IRON_BLOCK);
        blockTypes.put("event", Blocks.DIAMOND_BLOCK);
        blockTypes.put("player_action", Blocks.COBBLESTONE);
        blockTypes.put("if_player", Blocks.OAK_PLANKS);
        blockTypes.put("else", Blocks.END_STONE);
        blockTypes.put("call_func", Blocks.LAPIS_ORE);
        blockTypes.put("entity_event", Blocks.GOLD_BLOCK);
        blockTypes.put("process", Blocks.EMERALD_BLOCK);
        blockTypes.put("repeat", Blocks.PRISMARINE);
        blockTypes.put("entity_action", Blocks.MOSSY_COBBLESTONE);
        blockTypes.put("start_process", Blocks.EMERALD_ORE);
        blockTypes.put("if_var", Blocks.OBSIDIAN);
        blockTypes.put("if_entity", Blocks.BRICKS);
        blockTypes.put("control", Blocks.COAL_BLOCK);
        blockTypes.put("select_obj", Blocks.PURPUR_BLOCK);
        blockTypes.put("if_game", Blocks.RED_NETHER_BRICKS);
        blockTypes.put("game_action", Blocks.NETHERRACK);

        noStones.add("if_player");
        noStones.add("else");
        noStones.add("repeat");
        noStones.add("if_var");
        noStones.add("if_entity");
        noStones.add("if_game");

        noChests.add("else");
        noChests.add("call_func");
        noChests.add("event");
        noChests.add("entity_event");

        Minecraft mc = Recode.MC;
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, outline) -> {
            if (!Config.getBoolean("templatePeeking")) {
                return true;
            }

            BlockState chest = Blocks.CHEST.defaultBlockState();
            chest = chest.setValue(ChestBlock.FACING, Direction.NORTH);

            templatePreview = true;
            templateFits = true;
            try {
                blockRenderer.clear();

                //Save current matrix state
                PoseStack matrix = ctx.matrixStack();
                matrix.pushPose();

                //Offset camera
                Vec3 vec = ctx.camera().getPosition();
                matrix.translate(-vec.x(), -vec.y(), -vec.z());

                //Actual Render

                BlockPos target = new BlockPos(mc.hitResult.getLocation());

                if (!mc.level.getBlockState(target.below()).isAir()
                    && mc.player.isCreative()) {

                    if (TemplateUtils.isTemplate(mc.player.getMainHandItem())) {
                        JsonObject encoded = TemplateUtils.fromItemStack(
                            mc.player.getMainHandItem());
                        JsonArray json = JsonParser.parseString(new String(
                            CompressionUtil.fromGZIP(CompressionUtil.fromBase64(
                                encoded.get("code").getAsString().getBytes()))
                        )).getAsJsonObject().get("blocks").getAsJsonArray();

                        BlockPos dloc = target;

                        for (JsonElement b : json) {
                            JsonObject blocks = b.getAsJsonObject();
                            if (Objects.equals(blocks.get("id").getAsString(), "block")) {
                                String type = blocks.get("block").getAsString();

                                canFit(dloc);
                                blockRenderer.addBlocks(new BlockRenderer.RenderingBlock(dloc, blockTypes.get(type).defaultBlockState(), BlockRenderer.BlockType.BLOCK, true));

                                if (!noChests.contains(type)) {
                                    canFit(dloc.above());
                                    blockRenderer.addBlocks(new BlockRenderer.RenderingBlock(dloc.above(), chest, BlockRenderer.BlockType.CHEST, true));
                                }
                                if (!type.equals("else")) {
                                    ArrayList<CodeBlock> blocknames = ActionDump.getCodeBlock(type);
                                    if(blocknames.size() == 0) return true;
                                    String name = blocknames.get(0).getName();

                                    String action = "";
                                    String subAction = "";
                                    String inverted = "";

                                    if (blocks.has("action")) {
                                        action = blocks.get("action").getAsString();
                                    } else if (blocks.has("data")) {
                                        action = blocks.get("data").getAsString();
                                    }
                                    if (blocks.has("subAction")) {
                                        subAction = blocks.get("subAction").getAsString();
                                    }
                                    if (blocks.has("inverted")) {
                                        inverted = blocks.get("inverted").getAsString();
                                    }
                                    canFit(dloc.west());
                                    blockRenderer.addBlocks(new BlockRenderer.RenderingBlock(dloc.west(), getSign(name, action, subAction, inverted), BlockRenderer.BlockType.SIGN, true));
                                }
                                if (!noStones.contains(type)) {
                                    dloc = dloc.south();
                                    canFit(dloc);
                                    blockRenderer.addBlocks(new BlockRenderer.RenderingBlock(dloc, Blocks.STONE.defaultBlockState(), BlockRenderer.BlockType.BLOCK, true));
                                }

                            } else {
                                boolean open =
                                    Objects.equals(blocks.get("direct").getAsString(), "open");
                                boolean norm =
                                    Objects.equals(blocks.get("type").getAsString(), "norm");

                                BlockState bstate = Blocks.PISTON.defaultBlockState();
                                if (!norm) {
                                    bstate = Blocks.STICKY_PISTON.defaultBlockState();
                                }

                                if (open) {
                                    bstate = bstate.setValue(BlockStateProperties.FACING, Direction.SOUTH);
                                } else {
                                    dloc = dloc.south();
                                }
                                canFit(dloc);
                                blockRenderer.addBlocks(new BlockRenderer.RenderingBlock(dloc, bstate, BlockRenderer.BlockType.BLOCK, true));
                            }
                            dloc = dloc.south();
                        }
                        blockRenderer.render(ctx.world(), matrix, ctx.consumers(), templateFits ? new Color(125, 214, 246, 120) : new Color(250, 135, 135, 140));
                    }
                }

                //Undo changes to matrix
                matrix.popPose();
            } catch (Exception err) {
                err.printStackTrace();
            }
            templatePreview = false;

            return true;
        });
    }

    private SignBlockEntity getSign(String name, String action, String subAction, String inverted) {
        BlockState state = Blocks.OAK_WALL_SIGN.defaultBlockState();
        state = state.setValue(WallSignBlock.FACING, Direction.WEST);

        SignBlockEntity sign = new SignBlockEntity();

        String statename = FabricLoader.getInstance().isDevelopmentEnvironment() ?  "cachedState" : "field_11866";

        try {
            Class<BlockEntity> cl = BlockEntity.class;
            Field f = cl.getDeclaredField(statename);
            f.setAccessible(true);
            f.set(sign,state);
        } catch (Exception err) {
            err.printStackTrace();
        }

        sign.setMessage(0, new TextComponent(name));
        sign.setMessage(1, new TextComponent(action));
        sign.setMessage(2, new TextComponent(subAction));
        sign.setMessage(3, new TextComponent(inverted));

        return sign;
    }

    private void canFit(BlockPos pos) {
        templateFits = Minecraft.getInstance().level.getBlockState(pos).isAir() && templateFits;
    }
}
*/