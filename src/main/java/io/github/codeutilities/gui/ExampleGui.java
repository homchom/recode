package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import net.minecraft.text.LiteralText;

public class ExampleGui extends LightweightGuiDescription {

   public ExampleGui() {
      WGridPanel root = new WGridPanel();
      setRootPanel(root);
      root.setSize(256, 240);

      WLabel label = new WLabel(new LiteralText("Test"));
      root.add(label, 1, 1, 2, 1);

      root.validate(this);
   }
}
