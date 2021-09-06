package io.github.codeutilities.sys.renderer.block;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class BlockRenderer {
    private ArrayList<RenderingBlock> blocks;

    public BlockRenderer() {
        this.blocks = new ArrayList<>();
    }

    public BlockRenderer addBlocks(RenderingBlock ...blocks) {
        this.blocks.addAll(Arrays.asList(blocks));
        return this;
    }

    public void clear() {
        this.blocks.clear();
    }

    public void render(World world, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, Color color) {
        this.blocks.forEach(renderingBlock -> {
            BlockPos pos = renderingBlock.getBlockPos();
            BlockState state = renderingBlock.getBlockState();

            if(state != null) {
                if (state.getProperties().contains(Properties.FACING)) {
                    Direction facing = state.get(Properties.FACING);
                    if (!(facing == Direction.DOWN || facing == Direction.UP)) {
                        int rotation = Arrays.asList(Direction.values()).indexOf(state.get(Properties.FACING)) - 2;
                        for (int i = 0; i < rotation; i++) {
                            state.rotate(BlockRotation.CLOCKWISE_90);
                        }
                    }
                }
            }

            matrix.push();
            matrix.translate(pos.getX(), pos.getY(), pos.getZ());
            Random random = new Random();
            random.setSeed(42L);
            switch(renderingBlock.getRenderType()) {
                case BLOCK:
                    this.renderBlock(state, pos, world, matrix, vertexConsumerProvider.getBuffer(BetterRenderLayers.flexible()), renderingBlock.culling(), random, color);
                    break;
                case CHEST:
                    new ChestRenderer().render(world, pos, matrix, vertexConsumerProvider, 16777215, OverlayTexture.DEFAULT_UV, color);
                    break;
                case SIGN:
                    new SignRenderer().render(renderingBlock.getSignBlockEntity(), matrix, vertexConsumerProvider, 16777215, OverlayTexture.DEFAULT_UV, color);
                    break;
            }
            matrix.pop();
        });

    }








    //BLOCKS

    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(2048, 0.25F) {
            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });

    private boolean renderBlock(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random, Color color) {
        try {
            BlockRenderType blockRenderType = state.getRenderType();
            return blockRenderType != BlockRenderType.MODEL ? false : this.render(world, MinecraftClient.getInstance().getBlockRenderManager().getModel(state), state, pos, matrix, vertexConsumer, cull, random, state.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV, color);
        } catch (Throwable var11) {
            CrashReport crashReport = CrashReport.create(var11, "Tesselating block in world");
            CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, pos, state);
            throw new CrashException(crashReport);
        }
    }

    private void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean useQuadColorData, VertexConsumer vc) {
        int[] is = quad.getVertexData();
        Vec3i vec3i = quad.getFace().getVector();
        Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
        Matrix4f matrix4f = matrixEntry.getModel();
        vector3f.transform(matrixEntry.getNormal());
        int j = is.length / 8;
        MemoryStack memoryStack = MemoryStack.stackPush();
        Throwable var17 = null;

        try {
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            for(int k = 0; k < j; ++k) {
                ((Buffer)intBuffer).clear();
                intBuffer.put(is, k * 8, 8);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                float r;
                float s;
                float t;
                float v;
                float w;
                if (useQuadColorData) {
                    float l = (float)(byteBuffer.get(12) & 255) / 255.0F;
                    v = (float)(byteBuffer.get(13) & 255) / 255.0F;
                    w = (float)(byteBuffer.get(14) & 255) / 255.0F;
                    r = l * brightnesses[k] * red;
                    s = v * brightnesses[k] * green;
                    t = w * brightnesses[k] * blue;
                } else {
                    r = brightnesses[k] * red;
                    s = brightnesses[k] * green;
                    t = brightnesses[k] * blue;
                }

                int u = lights[k];
                v = byteBuffer.getFloat(16);
                w = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
                vector4f.transform(matrix4f);
                vc.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), r, s, t, alpha, v, w, overlay, u, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        } catch (Throwable var38) {
            var17 = var38;
            throw var38;
        } finally {
            if (memoryStack != null) {
                if (var17 != null) {
                    try {
                        memoryStack.close();
                    } catch (Throwable var37) {
                        var17.addSuppressed(var37);
                    }
                } else {
                    memoryStack.close();
                }
            }

        }

    }

    private boolean render(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay, Color color) {
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
        Vec3d vec3d = state.getModelOffset(world, pos);
        matrix.translate(vec3d.x, vec3d.y, vec3d.z);

        //try {
            return this.renderFlat(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay, color);
        /*} catch (Throwable var17) {
            CrashReport crashReport = CrashReport.create(var17, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, pos, state);
            crashReportSection.add("Using AO", bl);
            throw new CrashException(crashReport);
        }*/
    }

    private boolean renderFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long l, int i, Color color) {
        boolean bl = false;
        BitSet bitSet = new BitSet(3);
        Direction[] var14 = Direction.values();
        int var15 = var14.length;

        for(int var16 = 0; var16 < var15; ++var16) {
            Direction direction = var14[var16];
            random.setSeed(l);
            java.util.List<BakedQuad> list = model.getQuads(state, direction, random);
            if (!list.isEmpty() && (!cull || shouldDrawSide(state, world, pos, direction))) {
                int j = WorldRenderer.getLightmapCoordinates(world, state, pos.offset(direction));
                renderQuadsFlat(world, state, pos, j, i, false, buffer, vertexConsumer, list, bitSet, color);
                bl = true;
            }
        }

        random.setSeed(l);
        java.util.List<BakedQuad> list2 = model.getQuads(state, (Direction)null, random);
        if (!list2.isEmpty()) {
            renderQuadsFlat(world, state, pos, -1, i, false, buffer, vertexConsumer, list2, bitSet, color);
            bl = true;
        }

        return bl;
    }

    private void renderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, BitSet flags, Color color) {
        Iterator var11 = quads.iterator();

        while(var11.hasNext()) {
            BakedQuad bakedQuad = (BakedQuad)var11.next();
            if (useWorldLight) {
                getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), (float[])null, flags);
                BlockPos blockPos = flags.get(0) ? pos.offset(bakedQuad.getFace()) : pos;
                light = WorldRenderer.getLightmapCoordinates(world, state, blockPos);
            }

            float f = world.getBrightness(bakedQuad.getFace(), bakedQuad.hasShade());
            renderQuad(vertexConsumer, matrices.peek(), bakedQuad, f, f, f, f, light, light, light, light, overlay, color);
        }

    }

    private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
        float f = 32.0F;
        float g = 32.0F;
        float h = 32.0F;
        float i = -32.0F;
        float j = -32.0F;
        float k = -32.0F;

        int p;
        float r;
        for(p = 0; p < 4; ++p) {
            r = Float.intBitsToFloat(vertexData[p * 8]);
            float n = Float.intBitsToFloat(vertexData[p * 8 + 1]);
            float o = Float.intBitsToFloat(vertexData[p * 8 + 2]);
            f = Math.min(f, r);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, r);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }

        if (box != null) {
            box[Direction.WEST.getId()] = f;
            box[Direction.EAST.getId()] = i;
            box[Direction.DOWN.getId()] = g;
            box[Direction.UP.getId()] = j;
            box[Direction.NORTH.getId()] = h;
            box[Direction.SOUTH.getId()] = k;
            p = Direction.values().length;
            box[Direction.WEST.getId() + p] = 1.0F - f;
            box[Direction.EAST.getId() + p] = 1.0F - i;
            box[Direction.DOWN.getId() + p] = 1.0F - g;
            box[Direction.UP.getId() + p] = 1.0F - j;
            box[Direction.NORTH.getId() + p] = 1.0F - h;
            box[Direction.SOUTH.getId() + p] = 1.0F - k;
        }

        switch(face) {
            case DOWN:
                flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
                flags.set(0, g == j && (g < 1.0E-4F || state.isFullCube(world, pos)));
                break;
            case UP:
                flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
                flags.set(0, g == j && (j > 0.9999F || state.isFullCube(world, pos)));
                break;
            case NORTH:
                flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
                flags.set(0, h == k && (h < 1.0E-4F || state.isFullCube(world, pos)));
                break;
            case SOUTH:
                flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
                flags.set(0, h == k && (k > 0.9999F || state.isFullCube(world, pos)));
                break;
            case WEST:
                flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
                flags.set(0, f == i && (f < 1.0E-4F || state.isFullCube(world, pos)));
                break;
            case EAST:
                flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
                flags.set(0, f == i && (i > 0.9999F || state.isFullCube(world, pos)));
        }

    }

    private void renderQuad(VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int light0, int light1, int light2, int light3, int overlay, Color color) {
        quad(matrixEntry, quad, new float[]{brightness0, brightness1, brightness2, brightness3}, color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f, new int[]{light0, light1, light2, light3}, overlay, true, vertexConsumer);
    }

    private boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction facing) {
        BlockPos blockPos = pos.offset(facing);
        BlockState blockState = world.getBlockState(blockPos);
        List<BlockPos> positions = this.blocks.stream().filter(renderingBlock -> renderingBlock.getRenderType() != BlockType.SIGN).map(renderingBlock -> renderingBlock.getBlockPos()).collect(Collectors.toList());
        if(positions.contains(blockPos)) {
            blockState = this.blocks.stream().filter(renderingBlock -> renderingBlock.getRenderType() != BlockType.SIGN).collect(Collectors.toList()).get(positions.indexOf(blockPos)).getBlockState();
        }
        if (state.isSideInvisible(blockState, facing)) {
            return false;
        } else {
            Block.NeighborGroup neighborGroup = new Block.NeighborGroup(state, blockState, facing);
            Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap) FACE_CULL_MAP.get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
            if (b != 127) {
                return b != 0;
            } else {
                VoxelShape voxelShape = state.getCullingFace(world, pos, facing);
                VoxelShape voxelShape2 = blockState.getCullingFace(world, blockPos, facing.getOpposite());
                boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
                if (object2ByteLinkedOpenHashMap.size() == 2048) {
                    object2ByteLinkedOpenHashMap.removeLastByte();
                }

                object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte) (bl ? 1 : 0));
                return bl;
            }
        }
    }

    private void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j, Color color) {
        BakedQuad bakedQuad;
        for(Iterator var8 = list.iterator(); var8.hasNext(); this.quad(entry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f, new int[]{i, i, i, i}, j, false, vertexConsumer)) {
            bakedQuad = (BakedQuad)var8.next();
        }

    }






    public static class BetterRenderLayers extends RenderLayer {
        public BetterRenderLayers(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
            super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        }
        public static RenderLayer flexible() {
            RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DISABLE_DIFFUSE_LIGHTING).lightmap(DISABLE_LIGHTMAP).overlay(DISABLE_OVERLAY_COLOR).build(false);
            return of("flexible", VertexFormats.POSITION_COLOR_TEXTURE, 7, 256, true, true, multiPhaseParameters);
        }
        public static RenderLayer flexibleCustomTexture(Identifier id) {
            RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new Texture(id, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DISABLE_DIFFUSE_LIGHTING).lightmap(DISABLE_LIGHTMAP).overlay(DISABLE_OVERLAY_COLOR).build(false);
            return of("flexible_custom", VertexFormats.POSITION_COLOR_TEXTURE, 7, 262144, true, true, multiPhaseParameters);
        }
    }

    public enum BlockType {
        BLOCK(),
        CHEST(),
        SIGN();
    }

    public static class RenderingBlock {
        private final BlockPos pos;
        private final BlockState state;
        private final SignBlockEntity sign;
        private final BlockType renderType;
        private final boolean culling;

        /**
         *
         * @param pos
         * @param state
         * @param renderType
         * @param culling culling is when minecraft prevents faces you cant see from being rendered (eg template peeker)
         */
        public RenderingBlock(BlockPos pos, BlockState state, BlockType renderType, boolean culling) {
            this.pos = pos;
            this.state = state;
            this.renderType = renderType;
            this.culling = culling;
            this.sign = null;
        }

        public RenderingBlock(BlockPos pos, SignBlockEntity sign, BlockType renderType, boolean culling) {
            this.pos = pos;
            this.state = null;
            this.renderType = renderType;
            this.culling = culling;
            this.sign = sign;
            this.sign.setPos(pos);
        }

        public BlockState getBlockState() {
            return state;
        }

        public BlockPos getBlockPos() {
            return pos;
        }

        public BlockType getRenderType() {
            return renderType;
        }

        public boolean culling() {
            return culling;
        }

        public SignBlockEntity getSignBlockEntity() {
            return sign;
        }

        @Override
        public String toString() {
            return "RenderingBlock{" +
                    "pos=" + pos +
                    ", state=" + state +
                    ", sign=" + sign +
                    ", renderType=" + renderType +
                    ", culling=" + culling +
                    '}';
        }
    }
}