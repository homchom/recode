package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WItem;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

public class CItem extends WItem {

   Runnable onclick;
   String hover = "";

   public CItem(ItemStack stack) {
      super(stack);
   }

   public void onClick(int x, int y, int button) {
      if (onclick != null) onclick.run();
   }

   public void setClickListener(Runnable r) {
      onclick = r;
   }

   public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
      super.paint(matrices, x, y, mouseX, mouseY);
      if (hover.isEmpty()) return;
      if (mouseX >= 0 && mouseY >= 0 && mouseX < this.getWidth() && mouseY < this.getHeight()) {
         Screen screen = MinecraftClient.getInstance().currentScreen;
         assert screen != null;
         screen.renderTooltip(matrices, Collections.singletonList(new LiteralText(hover)), mouseX + x, mouseY + y);
      }
   }
}
