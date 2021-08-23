package io.github.codeutilities.mod.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.file.ILoader;
import io.github.codeutilities.sys.hypercube.templates.CompressionUtil;
import io.github.codeutilities.sys.hypercube.templates.TemplateUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
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

    public static BlockEntityRenderDispatcher berdContext;

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
        blockTypes.put("select_obj", Blocks.PURPUR_BLOCK);
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
        noChests.add("event");
        noChests.add("entity_event");

        MinecraftClient mc = CodeUtilities.MC;
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, outline) -> {
            if (!Config.getBoolean("templatePeeking")) {
                return true;
            }

            BlockState chest = Blocks.CHEST.getDefaultState();
            chest = chest.with(ChestBlock.FACING, Direction.NORTH);

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
                                    renderBlock(chest, dloc.up(), ctx);
                                }
                                if (!type.equals("else")) {
                                    String name = type.toUpperCase().replaceAll("_", " ");
                                    if (name.equals("EVENT")) {
                                        name = "PLAYER EVENT";
                                    }
                                    if (name.equals("FUNC")) {
                                        name = "FUNCTION";
                                    }
                                    if (name.equals("SET VAR")) {
                                        name = "SET VARIABLE";
                                    }
                                    if (name.equals("IF VAR")) {
                                        name = "IF VARIABLE";
                                    }
                                    if (name.equals("SELECT OBJ")) {
                                        name = "SELECT OBJECT";
                                    }
                                    if (name.equals("CALL FUNC")) {
                                        name = "CALL FUNCTiON";
                                    }

                                    String action = "";
                                    String subAction = "";
                                    String inverted = "";

                                    if (blocks.has("action")) {
                                        action = blocks.get("action").getAsString();
                                    }
                                    if (blocks.has("subAction")) {
                                        subAction = blocks.get("subAction").getAsString();
                                    }
                                    if (blocks.has("inverted")) {
                                        inverted = blocks.get("inverted").getAsString();
                                    }

                                    renderSign(name, action, subAction, inverted, dloc.west(), ctx);
                                }
                                if (!noStones.contains(type)) {
                                    dloc = dloc.south();
                                    renderBlock(Blocks.STONE.getDefaultState(), dloc, ctx);
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
                                    dloc = dloc.south();
                                }

                                renderBlock(bstate, dloc, ctx);
                            }

                            if (!mc.world.getBlockState(dloc).isAir() || mc.world.getBlockState(
                                dloc.down()).isAir()) {
                                mc.player.sendMessage(
                                    new LiteralText("§c§lWarning: §6Invalid Template Placement!"),
                                    true);
                            }
                            dloc = dloc.south();
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

    private void renderSign(String name, String action, String subAction, String inverted,
        BlockPos pos, WorldRenderContext ctx) {
        if (berdContext == null) {
            return;
        }
        SignBlockEntityRenderer r = new SignBlockEntityRenderer(berdContext);

        BlockState state = Blocks.OAK_WALL_SIGN.getDefaultState();
        state = state.with(WallSignBlock.FACING, Direction.WEST);

        SignBlockEntity sign = new SignBlockEntity();
        sign.setPos(pos);

        String statename = FabricLoader.getInstance().isDevelopmentEnvironment() ?  "cachedState" : "field_11866";

        try {
            Class<BlockEntity> cl = BlockEntity.class;
            Field f = cl.getDeclaredField(statename);
            f.setAccessible(true);
            f.set(sign,state);
        } catch (Exception err) {
            err.printStackTrace();
        }

        sign.setTextOnRow(0, new LiteralText(name));
        sign.setTextOnRow(1, new LiteralText(action));
        sign.setTextOnRow(2, new LiteralText(subAction));
        sign.setTextOnRow(3, new LiteralText(inverted));

        MatrixStack matrix = ctx.matrixStack();
        matrix.push();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        r.render(sign, 0, matrix, ctx.consumers(), 16777215, OverlayTexture.DEFAULT_UV);
        matrix.pop();
    }

    private void renderBlock(BlockState state, BlockPos pos, WorldRenderContext ctx) {
        MatrixStack matrix = ctx.matrixStack();
        MinecraftClient mc = CodeUtilities.MC;
        matrix.push();
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());
        mc.getBlockRenderManager()
            .renderBlockAsEntity(state, matrix, ctx.consumers(), 16777215,
                OverlayTexture.DEFAULT_UV);
        matrix.pop();
    }
}
