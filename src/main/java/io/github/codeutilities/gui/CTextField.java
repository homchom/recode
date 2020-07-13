package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.text.Text;

public class CTextField extends WTextField {

   public CTextField(Text suggestion) {
      this.suggestion = suggestion;
   }

   @Override
   public void onKeyPressed(int ch, int key, int modifiers) {
      super.onKeyPressed(ch,key,modifiers);
      onChanged.accept(text);
   }

}
