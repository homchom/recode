package io.github.homchom.recode.feature.rendering

import com.google.gson.JsonParser
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.sys.hypercube.templates.CompressionUtil
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.renderer.Sheets
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.WallSignBlock
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.awt.Color

val blockTypes = mapOf(
    "func" to Blocks.LAPIS_BLOCK.defaultBlockState(),
    "set_var" to Blocks.IRON_BLOCK.defaultBlockState(),
    "event" to Blocks.DIAMOND_BLOCK.defaultBlockState(),
    "player_action" to Blocks.COBBLESTONE.defaultBlockState(),
    "if_player" to Blocks.OAK_PLANKS.defaultBlockState(),
    "else" to Blocks.END_STONE.defaultBlockState(),
    "call_func" to Blocks.LAPIS_ORE.defaultBlockState(),
    "entity_event" to Blocks.GOLD_BLOCK.defaultBlockState(),
    "process" to Blocks.EMERALD_BLOCK.defaultBlockState(),
    "repeat" to Blocks.PRISMARINE.defaultBlockState(),
    "entity_action" to Blocks.MOSSY_COBBLESTONE.defaultBlockState(),
    "start_process" to Blocks.EMERALD_ORE.defaultBlockState(),
    "if_var" to Blocks.OBSIDIAN.defaultBlockState(),
    "if_entity" to Blocks.BRICKS.defaultBlockState(),
    "control" to Blocks.COAL_BLOCK.defaultBlockState(),
    "select_obj" to Blocks.PURPUR_BLOCK.defaultBlockState(),
    "if_game" to Blocks.RED_NETHER_BRICKS.defaultBlockState(),
    "game_action" to Blocks.NETHERRACK.defaultBlockState(),
)

val blockNames = mapOf(
    "func" to "Function",
    "set_var" to "Set Variable",
    "event" to "Event",
    "player_action" to "Player Action",
    "if_player" to "If Player",
    "else" to "Else",
    "call_func" to "Call Function",
    "entity_event" to "Entity Event",
    "process" to "Process",
    "repeat" to "Repeat",
    "entity_action" to "Entity Action",
    "start_process" to "Start Process",
    "if_var" to "If Variable",
    "if_entity" to "If Entity",
    "control" to "Control",
    "select_obj" to "Select Object",
    "if_game" to "If Game",
    "game_action" to "Game Action"
)

val noStones = setOf("if_player", "else", "repeat", "if_var", "if_entity", "if_game")

val noChests = setOf("else", "call_func", "event", "entity_event")

interface RenderBlock {
    fun render(ctx: WorldRenderContext, color: Color)
    val pos: BlockPos
}

data class NormalRenderBlock(override val pos: BlockPos, val state: BlockState) : RenderBlock {
    override fun render(ctx: WorldRenderContext, color: Color) {
        ctx.matrixStack().pushPose()
        ctx.matrixStack().translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        mc.blockRenderer.modelRenderer.renderModel(
            ctx.matrixStack().last(),
            ctx.consumers()!!.getBuffer(Sheets.translucentCullBlockSheet()),
            state,
            mc.blockRenderer.getBlockModel(state),
            1F, 1F, 1F,
            16777215,
            655360
        )
        ctx.matrixStack().popPose()
    }
}

data class ChestRenderBlock(override val pos: BlockPos, val state: BlockState) : RenderBlock {
    override fun render(ctx: WorldRenderContext, color: Color) {
        ctx.matrixStack().pushPose()
        ctx.matrixStack().translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

        val chestBlockEntity = ChestBlockEntity(BlockPos.ZERO, state)
        val chestRenderer = mc.blockEntityRenderDispatcher.getRenderer(chestBlockEntity)

        chestRenderer!!.render(
            chestBlockEntity,
            ctx.tickDelta(),
            ctx.matrixStack(),
            mc.renderBuffers().bufferSource(),
            16777215,
            655360
        )

        ctx.matrixStack().popPose()
    }
}

data class SignRenderBlock(override val pos: BlockPos, var state: BlockState, val rows: SignRows) : RenderBlock {
    override fun render(ctx: WorldRenderContext, color: Color) {
        ctx.matrixStack().pushPose()
        ctx.matrixStack().translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())

        val signBlockEntity = SignBlockEntity(BlockPos.ZERO, state)

        signBlockEntity.setMessage(0, Component.literal(rows.row0))
        signBlockEntity.setMessage(1, Component.literal(rows.row1))
        signBlockEntity.setMessage(2, Component.literal(rows.row2))
        signBlockEntity.setMessage(3, Component.literal(rows.row3))

        val signRenderer = mc.blockEntityRenderDispatcher.getRenderer(signBlockEntity)

        signRenderer!!.render(
            signBlockEntity,
            ctx.tickDelta(),
            ctx.matrixStack(),
            mc.renderBuffers().bufferSource(),
            16777215,
            655360
        )

        ctx.matrixStack().popPose()
    }
}

data class SignRows(val row0: String, val row1: String, val row2: String, val row3: String)

val FTemplatePeeker = feature("Template Peeker") {
    onLoad {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register { ctx, _ ->
            if (!Config.getBoolean("templatePeeking")
                || mc.player == null
                || !mc.player!!.isCreative
            ) {
                return@register true
            }

            ctx.matrixStack().pushPose()

            val camPos = ctx.camera().position
            ctx.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z)

            try {
                val chestState = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH)
                val todo = mutableSetOf<RenderBlock>()

                val signState = Blocks.OAK_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.WEST)

                if (TemplateUtil.isTemplate(mc.player!!.mainHandItem)) {
                    val json = JsonParser.parseString(
                        String(
                            CompressionUtil.fromGZIP(
                                CompressionUtil.fromBase64(
                                    TemplateUtil.read(
                                        mc.player!!.mainHandItem
                                    ).get("code").asString.encodeToByteArray()
                                )
                            )
                        )
                    ).asJsonObject.get("blocks").asJsonArray

                    var currentLoc = BlockPos(mc.hitResult!!.location)

                    if (!mc.level!!.getBlockState(currentLoc.below()).isAir) {
                        for (b in json) {
                            val block = b.asJsonObject
                            if (block["id"].asString == "block") {
                                val type = block["block"].asString

                                todo.add(NormalRenderBlock(currentLoc, blockTypes[type]!!))

                                if (!noChests.contains(type)) {
                                    todo.add(ChestRenderBlock(currentLoc.above(), chestState))
                                }

                                if (type != "else") {
                                    val signRows = SignRows(
                                        blockNames.getOrDefault(type, type),
                                        if (block.has("action")) block["action"].asString else block["data"].asString,
                                        if (block.has("subAction")) block["subAction"].asString else "",
                                        if (block.has("inverted")) block["inverted"].asString else ""
                                    )

                                    todo.add(SignRenderBlock(currentLoc.west(), signState, signRows))
                                }

                                if (!noStones.contains(type)) {
                                    todo.add(NormalRenderBlock(currentLoc.south(), Blocks.STONE.defaultBlockState()))
                                    currentLoc = currentLoc.south()
                                }
                            } else {
                                val open = block["direct"].asString == "open"
                                val norm = block["type"].asString == "norm"

                                var state =
                                    if (norm) Blocks.PISTON.defaultBlockState() else Blocks.STICKY_PISTON.defaultBlockState()

                                if (open) {
                                    state = state.setValue(BlockStateProperties.FACING, Direction.SOUTH)
                                } else {
                                    currentLoc = currentLoc.south()
                                }

                                todo.add(NormalRenderBlock(currentLoc, state))
                            }
                            currentLoc = currentLoc.south()
                        }

                        var fits = true

                        for (renderBlock in todo) {
                            if (!mc.level!!.getBlockState(renderBlock.pos).isAir) {
                                fits = false
                                break
                            }
                        }

                        for (renderBlock in todo) {
                            if (mc.level!!.getBlockState(renderBlock.pos).isAir) {
                                renderBlock.render(
                                    ctx,
                                    if (fits) Color.BLUE else Color.RED
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            ctx.matrixStack().popPose()
            true
        }
    }
}