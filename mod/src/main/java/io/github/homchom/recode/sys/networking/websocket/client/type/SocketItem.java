package io.github.homchom.recode.sys.networking.websocket.client.type;

import net.minecraft.world.item.ItemStack;

public abstract class SocketItem {

    public abstract String getIdentifier();

    public abstract ItemStack getItem(String data) throws Exception;

}
