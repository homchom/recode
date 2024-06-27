package io.github.homchom.recode.util.item

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

/**
 * If this ItemStack is a DF ValueItem, returns its json object, otherwise return null.
 * ValueItem compounds are in the following format:
 *
 * ```json
 * {
 *     "id": <String>,
 *   "data": <Compound>
 * }
 * ```
 *
 * @return This item's DF ValueItem JsonObject, if one exists.
 */
fun ItemStack.getDFValueItemData() : JsonObject? {
    // TODO May need to change once Item Components release?
    val nbt = this.tag
        ?.getCompound("PublicBukkitValues")
        ?.getString("hypercube:varitem")

    if (nbt == null) {
        return null
    }

    return JsonParser.parseString(nbt) as? JsonObject
}