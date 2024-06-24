package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.game.SlotIndex
import io.github.homchom.recode.game.SlotOrder
import io.github.homchom.recode.mc
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

val Player.username: String get() = gameProfile.name

/**
 * @return A [Sequence] of this [Inventory]'s items in the order specified by [SlotOrder.InventoryItems].
 */
fun Inventory.asSequence() = sequenceOf(items, armor, offhand).flatten()

/**
 * Sets this player's creative [Inventory] [slot] to [stack] and synchronizes it with the server.
 *
 * @throws IllegalStateException if the player is not in creative mode.
 */
fun LocalPlayer.setItemAndSync(slot: SlotIndex<SlotOrder.InventoryItems>, stack: ItemStack) =
    setItemAndSync(slot, slot.reorder(SlotOrder.InventoryMenu), stack)

/**
 * Sets this player's creative [Inventory] [slot] to [stack] and synchronizes it with the server.
 *
 * @throws IllegalStateException if the player is not in creative mode.
 */
@JvmName("setItemAndSyncMenu")
fun LocalPlayer.setItemAndSync(slot: SlotIndex<SlotOrder.InventoryMenu>, stack: ItemStack) =
    setItemAndSync(slot.reorder(SlotOrder.InventoryItems), slot, stack)

private fun LocalPlayer.setItemAndSync(
    itemsSlot: SlotIndex<SlotOrder.InventoryItems>,
    menuSlot: SlotIndex<SlotOrder.InventoryMenu>,
    stack: ItemStack
) {
    check(isCreative) { "Inventory items can only be set by the client in creative mode" }
    inventory.setItem(itemsSlot(), stack)
    mc.gameMode?.handleCreativeModeItemAdd(stack, menuSlot())
}