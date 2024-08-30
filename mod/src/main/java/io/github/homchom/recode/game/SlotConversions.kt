package io.github.homchom.recode.game

import io.github.homchom.recode.util.InvokableWrapper

/**
 * An index of a [net.minecraft.world.Container] slot, ordered by [order]. The order is stored in type
 * parameter @T and can be changed, allowing for safe slot conversions.
 *
 * @see SlotOrder
 */
data class SlotIndex<out T : SlotOrder>(
    override val value: Int,
    private val order: T
) : InvokableWrapper<Int> {
    /**
     * Converts the index to one in order [order].
     */
    fun <S : SlotOrder> reorder(order: S) = converted(value, this.order, order)

    companion object {
        /**
         * @return A [SlotIndex] with order [targetOrder] and an index equivalent to [value] in [sourceOrder].
         */
        fun <S : SlotOrder> converted(value: Int, sourceOrder: SlotOrder, targetOrder: S): SlotIndex<S> {
            val (order, subIndex) = sourceOrder.getOrNull(value)
                ?: throw IndexOutOfBoundsException("Slot order $sourceOrder does not have index $value")

            val newIndex = targetOrder.indexOf(order, subIndex)
            require(newIndex != -1) {
                "Slot order $targetOrder does not have an index equivalent to $value in $sourceOrder"
            }

            return SlotIndex(newIndex, targetOrder)
        }
    }
}

/**
 * A recursive total ordering of [net.minecraft.world.Container] indices with identity.
 *
 * @see SlotIndex
 */
sealed interface SlotOrder { // TODO: add unit tests
    data object Hotbar : SlotOrder by slots(9)
    data object UpperInventory : SlotOrder by slots(27)
    data object Armor : SlotOrder by slots(4)
    data object Offhand : SlotOrder by slots(1)
    data object QuickCraft : SlotOrder by slots(5)

    data object InventoryItems : SlotOrder by slots(Hotbar, UpperInventory, Armor.reversed, Offhand)
    data object InventoryMenu : SlotOrder by slots(QuickCraft, Armor, UpperInventory, Hotbar, Offhand)

    /**
     * The identity of the order, used for equality checks.
     */
    val id get() = this

    /**
     * The number of slots present in this order.
     */
    val size: Int

    /**
     * @return The base [SlotOrder] found at [index], paired with the sub-index of [index] in it. Returns
     * `null` if [index] is out of bounds.
     */
    fun getOrNull(index: Int): Pair<SlotOrder, Int>?

    /**
     * @return The index of the given nested [order]'s [subIndex] in this order.
     */
    fun indexOf(order: SlotOrder, subIndex: Int): Int
}

private fun slots(size: Int) = SlotRange(size)
private fun slots(vararg compartments: SlotOrder) = SlotSequence(*compartments)
private val SlotOrder.reversed get() = ReversedSlotOrder(this)

private class SlotRange(override val size: Int) : SlotOrder {
    override fun getOrNull(index: Int) =
        if (index in 0..<size) id to index else null

    override fun indexOf(order: SlotOrder, subIndex: Int) =
        if (id == order.id) subIndex else -1
}

private class SlotSequence(vararg compartments: SlotOrder) : SlotOrder {
    override val size = compartments.sumOf(SlotOrder::size)

    private val compartments = compartments.toList()

    override fun getOrNull(index: Int): Pair<SlotOrder, Int>? {
        var mutIndex = index
        for (compartment in compartments) {
            compartment.getOrNull(mutIndex)?.let { return it }
            mutIndex -= compartment.size
        }
        return null
    }

    override fun indexOf(order: SlotOrder, subIndex: Int): Int {
        var index = 0
        for (compartment in compartments) {
            val nestedIndex = compartment.indexOf(order, subIndex)
            if (nestedIndex != -1) return index + nestedIndex
            index += compartment.size
        }
        return -1
    }
}

private class ReversedSlotOrder(private val delegate: SlotOrder) : SlotOrder {
    override val size get() = delegate.size

    override fun getOrNull(index: Int) =
        delegate.getOrNull(size - 1 - index)

    override fun indexOf(order: SlotOrder, subIndex: Int) =
        delegate.indexOf(order, order.size - 1 - subIndex)
}