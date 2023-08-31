package io.github.homchom.recode.sys.renderer.widgets;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

@Environment(EnvType.CLIENT)
public class CItem extends WItem {

    private Component[] texts = new Component[0];

    public CItem(ItemStack stack) {
        super(stack);
        setTooltip(stack.getTooltipLines(null, Default.NORMAL).toArray(new Component[0]));
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        tooltip.add(texts);
    }

    public void setTooltip(Component... text) {
        texts = text;
    }

    @Override
    public void paint(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            if (y > screen.height ||
                    x > screen.width ||
                    x < 0 || y < 0) {
                return;
            }
        }

        super.paint(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int x, int y, int tX, int tY) {
        super.renderTooltip(guiGraphics, x, y, tX, tY);

        /* Feature temporarily disabled
        if (Config.getBoolean("previewHeadSkin")) {
            if (item.getItem() == Items.PLAYER_HEAD) {
                try {
                    CompoundTag nbt = item.getTag();
                    if (nbt == null) return;
                    CompoundTag owner = nbt.getCompound("SkullOwner");
                    CompoundTag properties = owner.getCompound("Properties");
                    ListTag texture = properties.getList("textures",10);
                    String value = texture.getCompound(0).getString("Value");
                    String out = new String(Base64.decodeBase64(value));
                    JsonObject obj = JsonParser.parseString(out).getAsJsonObject();
                    String id = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                    id = StringUtils.substringAfter(id,"texture/");

                    TextureManager manager = Recode.MC.getTextureManager();

                    Identifier identifier = new Identifier("skin_preview_" + id);

                    if (manager.getTexture(identifier) == null) {
                        URL url = new URL("https://mc-heads.net/body/" + id);
                        identifier = Recode.MC.getTextureManager().registerDynamicTexture(
                            identifier.getPath(), new NativeImageBackedTexture(NativeImage.read(url.openStream()))
                        );
                    }
                    ScreenDrawing.texturedRect(0,0,50, 120, identifier, 0, 0, 1, 1, 0xffffff, 1f);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        }
        */
    }

}
