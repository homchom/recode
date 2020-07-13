package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WItem;
import net.minecraft.item.ItemStack;

public class CItem extends WItem {

   Runnable onclick;

   public CItem(ItemStack stack) {
      super(stack);
   }

   public void onClick(int x, int y, int button) {
      if (onclick != null) onclick.run();
   }

   public void setClickListener(Runnable r) {
      onclick = r;
   }
}
