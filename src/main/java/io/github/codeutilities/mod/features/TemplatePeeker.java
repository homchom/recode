package io.github.codeutilities.mod.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.file.ILoader;
import io.github.codeutilities.sys.hypercube.templates.CompressionUtil;
import io.github.codeutilities.sys.hypercube.templates.TemplateUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class TemplatePeeker implements ILoader {

    public static boolean templatePreview = false;
    public static HashMap<String, Block> blockTypes = new HashMap<>();
    public static HashSet<String> noStones = new HashSet<>();
    public static HashSet<String> noChests = new HashSet<>();

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
        blockTypes.put("entity_action", Blocks.PRISMARINE);
        blockTypes.put("start_process", Blocks.EMERALD_ORE);
        blockTypes.put("if_var", Blocks.OBSIDIAN);
        blockTypes.put("if_entity", Blocks.OBSIDIAN);
        blockTypes.put("control", Blocks.COAL_BLOCK);
        blockTypes.put("select_obj", Blocks.COAL_BLOCK);
        blockTypes.put("if_game", Blocks.NETHER_BRICKS);
        blockTypes.put("game_action", Blocks.NETHERRACK);

        noStones.add("if_player");
        noStones.add("else");
        noStones.add("repeat");
        noStones.add("if_var");
        noStones.add("if_entity");
        noStones.add("if_game");

        noChests.add("else");
        noChests.add("call_func");

        MinecraftClient mc = CodeUtilities.MC;
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, outline) -> {
            if (!Config.getBoolean("templatePeeking")) return true;

            templatePreview = true;
            try {
                //Save current matrix state
                MatrixStack matrix = ctx.matrixStack();
                matrix.push();

                //Offset camera
                Vec3d vec = ctx.camera().getPos();
                matrix.translate(-vec.getX(), -vec.getY(), -vec.getZ());

                //Actual Render

                BlockPos target = new BlockPos(mc.crosshairTarget.getPos());

                if (mc.world.getBlockState(target).isAir()
                    && !mc.world.getBlockState(target.down()).isAir()
                    && mc.player.isCreative()) {

                    if (TemplateUtils.isTemplate(mc.player.getMainHandStack())) {
                        JsonObject encoded = TemplateUtils.fromItemStack(
                            mc.player.getMainHandStack());
                        JsonArray json = CodeUtilities.JSON_PARSER.parse(new String(
                            CompressionUtil.fromGZIP(CompressionUtil.fromBase64(
                                encoded.get("code").getAsString().getBytes()))
                        )).getAsJsonObject().get("blocks").getAsJsonArray();

                        BlockPos dloc = target;

                        for (JsonElement b : json) {
                            JsonObject blocks = b.getAsJsonObject();
                            if (Objects.equals(blocks.get("id").getAsString(), "block")) {
                                String type = blocks.get("block").getAsString();

                                renderBlock(blockTypes.get(type).getDefaultState(), dloc, ctx);

                                if (!noChests.contains(type)) {
                                    //TODO: add signs and chests to template peeking
                                    //doesnt work, probs cuz its a tile entity
//                                    renderBlock(Blocks.CHEST.getDefaultState(), dloc.up(), ctx);
                                }
                                if (!noStones.contains(type)) {
                                    dloc = dloc.north();
                                    renderBlock(Blocks.STONE.getDefaultState(),dloc,ctx);
                                }

                            } else {
                                boolean open =
                                    Objects.equals(blocks.get("direct").getAsString(), "open");
                                boolean norm =
                                    Objects.equals(blocks.get("type").getAsString(), "norm");

                                BlockState bstate = Blocks.PISTON.getDefaultState();
                                if (!norm) {
                                    bstate = Blocks.STICKY_PISTON.getDefaultState();
                                }

                                if (!open) {
                                    bstate = bstate.with(Properties.FACING, Direction.SOUTH);
                                    dloc = dloc.north();
                                }

                                renderBlock(bstate, dloc, ctx);
                            }

                            if (!mc.world.getBlockState(dloc).isAir() || mc.world.getBlockState(dloc.down()).isAir()) {
                                mc.player.sendMessage(new LiteralText("§c§lWarning: §6Invalid Template Placement!"),true);
                            }
                            dloc = dloc.north();
                        }
                    }
                }

                //Undo changes to matrix
                matrix.pop();
            } catch (Exception err) {
                err.printStackTrace();
            }
            templatePreview = false;

            return true;
        });
    }

    private void renderBlock(BlockState state, BlockPos blockPos, WorldRenderContext ctx) {
        MatrixStack matrix = ctx.matrixStack();
        MinecraftClient mc = CodeUtilities.MC;
        ClientWorld world = ctx.world();
        VertexConsumer vertexConsumer = ctx.consumers().getBuffer(RenderLayer.getSolid());
        matrix.push();
        matrix.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Vec3d vec3d = state.getModelOffset(world, blockPos);
        matrix.translate(vec3d.x, vec3d.y, vec3d.z);
        BakedModel model = mc.getBlockRenderManager().getModel(state);
        mc.getBlockRenderManager().getModelRenderer()
            .renderSmooth(world, model, state, blockPos, matrix, vertexConsumer, true,
                new Random(0), 0, 0);
        matrix.pop();
    }
}
