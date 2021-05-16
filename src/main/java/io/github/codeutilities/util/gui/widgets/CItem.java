package io.github.codeutilities.util.gui.widgets;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext.Default;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CItem extends WItem {

    private Text[] texts = new Text[0];
    private final ItemStack item;

    public CItem(ItemStack stack) {
        super(stack);
        item = stack;
        setTooltip(stack.getTooltip(null, Default.NORMAL).toArray(new Text[0]));
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        tooltip.add(texts);
    }

    public void setTooltip(Text... text) {
        texts = text;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null) {
            if (y > screen.height ||
                    x > screen.width ||
                    x < 0 || y < 0) {
                return;
            }
        }

        super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int x, int y, int tX, int tY) {
        super.renderTooltip(matrices, x, y, tX, tY);

        /* Feature temporarily disabled
        if (CodeUtilsConfig.getBoolean("previewHeadSkin")) {
            if (item.getItem() == Items.PLAYER_HEAD) {
                try {
                    CompoundTag nbt = item.getTag();
                    if (nbt == null) return;
                    CompoundTag owner = nbt.getCompound("SkullOwner");
                    CompoundTag properties = owner.getCompound("Properties");
                    ListTag texture = properties.getList("textures",10);
                    String value = texture.getCompound(0).getString("Value");
                    String out = new String(Base64.decodeBase64(value));
                    JsonObject obj = CodeUtilities.JSON_PARSER.parse(out).getAsJsonObject();
                    String id = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                    id = StringUtils.substringAfter(id,"texture/");

                    TextureManager manager = CodeUtilities.MC.getTextureManager();

                    Identifier identifier = new Identifier("skin_preview_" + id);

                    if (manager.getTexture(identifier) == null) {
                        URL url = new URL("https://mc-heads.net/body/" + id);
                        identifier = CodeUtilities.MC.getTextureManager().registerDynamicTexture(
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
