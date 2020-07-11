package io.github.codeutilities.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.commands.item.CustomHeadCommand;
import io.github.codeutilities.util.Webutil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.LiteralText;

public class CustomHeadSearchGui extends LightweightGuiDescription {

   static List<JsonObject> heads = new ArrayList<>();

   public CustomHeadSearchGui() {
      WGridPanel root = new WGridPanel();
      setRootPanel(root);
      root.setSize(256, 240);

      WTextField searchbox = new WTextField(new LiteralText("Search..."));
      searchbox.setChangedListener(query -> {
         System.out.println(query);
      });
      root.add(searchbox, 0, 0, 15, 10);

      WText loading = new WText(new LiteralText("Loading... (0%)"));
      root.add(loading, 5, 2, 10,1);

      new Thread(() -> {
         try {
            if (heads.size() < 30000) {
               heads.clear();
               String[] categories = {"alphabet", "animals", "blocks", "decoration", "humans",
                   "humanoid", "miscellaneous", "monsters", "plants", "food-drinks"};
               int progress = 0;
               for (String cat : categories) {
                  String response = Webutil
                      .getString("https://minecraft-heads.com/scripts/api.php?cat=" + cat);
                  JsonArray headlist = new Gson().fromJson(response, JsonArray.class);
                  for (JsonElement head : headlist) {
                     heads.add((JsonObject) head);
                  }

                  progress++;
                  loading.setText(new LiteralText(
                      "Loading... (" + (progress* 100/categories.length) + "%)"));
               }
            }

            root.remove(loading);

            WGridPanel panel = new WGridPanel(1);
            WScrollPanel scrollPanel = new WScrollPanel(panel);
//            scrollPanel.setSize(256,300);
            scrollPanel.setScrollingVertically(TriState.TRUE);
            scrollPanel.setScrollingHorizontally(TriState.FALSE);
            root.add(scrollPanel, 0, 2, 15, 12);

            System.out.println("Heads: " + heads.size());

            int pos = 0;
            for (JsonObject head : heads) {
               //head -> name, uuid, value
               ItemStack item = new ItemStack(Items.PLAYER_HEAD);

               String name = head.get("name").getAsString();
               String value = head.get("value").getAsString();
               item.setTag(StringNbtReader.parse("{display:{Name:\"{\\\"text\\\":\\\"" + name + "\\\"}\"},SkullOwner:{Id:" + CustomHeadCommand.genId()
                   + ",Properties:{textures:[{Value:\"" + value + "\"}]}}}"));
               WItem i = new WItem(item);
               panel.add(i,pos%14*18,pos/14*18,18,18);
               pos++;
               if (pos/100*100 == pos) {
                  Thread.sleep(5000);
               }
            }
         } catch (Exception e) {
            loading.setText(new LiteralText("§cFailed to load!"));
            e.printStackTrace();
         }
      }).start();

      root.validate(this);
   }

}
