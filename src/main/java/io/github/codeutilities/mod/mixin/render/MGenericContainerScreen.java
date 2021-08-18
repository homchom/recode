package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.hypercube.codeaction.Action;
import io.github.codeutilities.sys.hypercube.codeaction.ActionDump;
import io.github.codeutilities.sys.hypercube.codeaction.DisplayItem;
import io.github.codeutilities.sys.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext.Default;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class MGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> {

    public MGenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String[] signt = CodeUtilities.signText;
        if (signt.length != 4) {
            return;
        }

        List<Action> actions = ActionDump.getActions(signt[1])
            .stream().filter(e -> Objects.equals(e.getCodeBlock().getName(), signt[0]))
            .filter(e -> Objects.equals(e.getName(), signt[1]))
            .collect(Collectors.toList());

        if (actions.size() == 1) {
            if (Config.getBoolean("showCodeblockDescription")) showDesc(actions.get(0), matrices);
            if (Config.getBoolean("showParameterErrors")) argCheck(actions.get(0), matrices);
        }
    }

    private void argCheck(Action a, MatrixStack matrices) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < handler.getInventory().size(); i++) {
            items.add(handler.getInventory().getStack(i));
        }
        DisplayItem ditem = a.getIcon();
        List<String> errors = new ArrayList<>();

        //ARGUMENTS HAVING OR IS ANNOYING

        int y = 0;
        for (String line : errors) {
            Text text = TextUtil.colorCodesToTextComponent("§c" + line);
            CodeUtilities.MC.textRenderer.draw(matrices, text, CodeUtilities.MC.currentScreen.width - textRenderer.getWidth(text) - 10, 10 + y, 0xffffff);
            y += 10;
        }
    }

    private void showDesc(Action a, MatrixStack matrices) {
        DisplayItem icon = a.getIcon();

        List<Text> desc = icon.toItemStack().getTooltip(CodeUtilities.MC.player, Default.NORMAL);

        int y = 0;
        for (Text line : desc) {
            CodeUtilities.MC.textRenderer.draw(matrices, line, 10, 10 + y, 0xffffff);
            y += 10;
        }
    }

}
