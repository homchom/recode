package io.github.homchom.recode.feature.rendering

import com.google.gson.*
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector4f
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.init.ClientStopEvent
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.sys.file.ExternalFile
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import io.github.homchom.recode.sys.util.ItemUtil
import io.github.homchom.recode.sys.util.SoundUtil
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.DetectedVersion
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.TagParser
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.io.File
import java.nio.file.Files

const val GRID_SIZE = 20

val pinnedVars = mutableListOf<ItemStack>()
val recentVars = mutableListOf<ItemStack>()

val file: File = ExternalFile.QUICK_VARS.path.toFile()

val FQuickChestVars = feature("Quick Chest Vars") {
    onLoad {
        if (file.exists()) {
            val json = JsonParser.parseString(Files.readString(file.toPath())) as JsonObject

            if (json.keySet().size != 0) {
                val dataVer = json.get("dataVer").asInt
                val pinned = json.getAsJsonArray("pinned")
                val recent = json.getAsJsonArray("recent")

                for (item in pinned) {
                    pinnedVars.add(ItemStack.of(updateItem(dataVer, TagParser.parseTag(item.asString))))
                }

                for (item in recent) {
                    recentVars.add(ItemStack.of(updateItem(dataVer, TagParser.parseTag(item.asString))))
                }
            }
        }

        ScreenEvents.AFTER_INIT.register { mc: Minecraft, screen: Screen, width: Int, height: Int ->
            if (mc.screen is ContainerScreen
                && Config.getBoolean("quickChestVars")
            ) {
                val xStart = width * 0.75
                val yStart = height * 0.25
                val xEnd = width - GRID_SIZE

                ScreenEvents.afterRender(screen)
                    .register { _: Screen, stack: PoseStack, mouseX: Int, mouseY: Int, _: Float ->
                        if (DFInfo.currentState.getMode() != LegacyState.Mode.DEV
                            || !mc.player!!.isCreative
                        ) {
                            return@register
                        }

                        stack.pushPose()

                        stack.translate(xStart, yStart, 0.0)

                        for (group in listOf(pinnedVars, recentVars)) {
                            var x = 0.0
                            for (item in group) {
                                val pos = getOrigin(stack)

                                mc.itemRenderer.renderGuiItem(
                                    item,
                                    pos.x().toInt(),
                                    pos.y().toInt()
                                )

                                if (pos.x() < mouseX
                                    && pos.y() < mouseY
                                    && pos.x() + GRID_SIZE > mouseX
                                    && pos.y() + GRID_SIZE > mouseY
                                ) {
                                    stack.pushPose()
                                    //mouse offset
                                    stack.translate(
                                        (mouseX - pos.x()).toDouble(),
                                        (mouseY - pos.y()).toDouble(),
                                        0.0
                                    )

                                    //pixel center
                                    val currPos = getOrigin(stack)
                                    stack.translate(
                                        (currPos.x() % 1).toDouble(),
                                        (currPos.y() % 1).toDouble(),
                                        0.0
                                    )
                                    screen.renderTooltip(
                                        stack,
                                        screen.getTooltipFromItem(item),
                                        item.tooltipImage,
                                        0,
                                        0
                                    )
                                    stack.popPose()
                                }

                                stack.translate(GRID_SIZE.toDouble(), 0.0, 0.0)
                                x += GRID_SIZE
                                if (getOrigin(stack).x() > xEnd) {
                                    stack.translate(-x, GRID_SIZE.toDouble(), 0.0)
                                    x = 0.0
                                }
                            }
                            stack.translate(-x, GRID_SIZE * 1.5, 0.0)
                        }

                        stack.popPose()
                    }

                ScreenMouseEvents.afterMouseClick(screen)
                    .register { _: Screen, mouseX: Double, mouseY: Double, button: Int ->
                        val relativeX = mouseX - xStart
                        var relativeY = mouseY - yStart

                        if (relativeX < 0.0
                            || relativeY < 0.0
                            || mouseX > xEnd
                        ) {
                            return@register
                        }

                        val gridX = (relativeX / GRID_SIZE).toInt()
                        val rowSize = ((xEnd - xStart) / GRID_SIZE).toInt() + 1
                        val pinnedSize = pinnedVars.size / rowSize * GRID_SIZE + GRID_SIZE

                        var area = pinnedVars

                        if (relativeY > pinnedSize) {
                            area = recentVars
                            relativeY -= pinnedSize + GRID_SIZE / 2
                        }

                        val gridY = (relativeY / GRID_SIZE).toInt()

                        val index = gridY * rowSize + gridX

                        if (area.size > index) {
                            if (button != 1) {
                                val freeSlot = (screen as ContainerScreen).menu.slots.find {
                                    it.item == ItemStack.EMPTY
                                }
                                if (freeSlot != null) {
                                    ItemUtil.setContainerItem(freeSlot.index, area[index])
                                    SoundUtil.playSound(SoundEvents.ITEM_PICKUP)
                                }
                            } else {
                                //un/pin the item
                                if (area == recentVars) {
                                    pinnedVars.add(area[index])
                                    SoundUtil.playSound(SoundEvents.ARROW_HIT)
                                } else {
                                    SoundUtil.playSound(SoundEvents.SHULKER_BULLET_HIT)
                                }
                                area.removeAt(index)
                            }
                        }

                    }
            }
        }

        ClientStopEvent.listen { _, _ ->
            val currentVer = DetectedVersion.BUILT_IN.dataVersion.version

            val json = JsonObject()
            json.addProperty("dataVer", currentVer)

            val pinnedArr = JsonArray()
            for (item in pinnedVars) {
                pinnedArr.add(item.save(CompoundTag()).asString)
            }
            json.add("pinned", pinnedArr)

            val recentArr = JsonArray()
            for (item in recentVars) {
                recentArr.add(item.save(CompoundTag()).asString)
            }
            json.add("recent", recentArr)

            Files.writeString(file.toPath(), json.toString())
        }
    }
}

fun recentItem(itemStack: ItemStack) {
    val item = itemStack.copy()

    if (item.item == Items.AIR) {
        return
    }

    getDfData(item) ?: return

    item.count = 1

    if (pinnedVars.any { same(it, item) }) {
        return
    }

    val i = recentVars.indexOfFirst { same(it, item) }
    if (i != -1) {
        recentVars.removeAt(i)
        recentVars.add(0, item)
        return
    }

    recentVars.add(0, item)
    if (recentVars.size > 50) {
        recentVars.removeAt(recentVars.size - 1)
    }
}

fun same(x: ItemStack, y: ItemStack): Boolean {
    val xData = getDfData(x) ?: return false
    val yData = getDfData(y) ?: return false

    val xJson = tryParse(xData) ?: return false
    val yJson = tryParse(yData) ?: return false

    return xJson == yJson
}

fun getDfData(item: ItemStack?): String? {
    if (item == null) return null

    val tag = item.getTagElement("PublicBukkitValues") ?: return null

    if (!tag.contains("hypercube:varitem")) return null

    return tag.getString("hypercube:varitem")
}

fun updateItem(dataVer: Int, tag: CompoundTag): CompoundTag {
    return NbtUtils.update(mc.fixerUpper, DataFixTypes.HOTBAR, tag, dataVer)
}

fun getOrigin(poseStack: PoseStack): Vector4f {
    val out = Vector4f(0f, 0f, 0f, 1f)
    out.transform(poseStack.last().pose())
    return out
}

fun tryParse(data: String): JsonElement? {
    return try {
        JsonParser.parseString(data)
    } catch (e: JsonSyntaxException) {
        null
    }
}